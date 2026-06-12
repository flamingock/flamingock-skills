#!/usr/bin/env bash
set -euo pipefail

DEFAULT_GROUP="io.flamingock"
DEFAULT_ARTIFACT="flamingock-community"

usage() {
    cat >&2 <<EOF
Usage:
  $0                         Resolve Flamingock from Gradle plugin and Maven metadata
  $0 --plugin                Resolve the io.flamingock Gradle plugin
  $0 --maven                 Resolve io.flamingock:flamingock-community
  $0 <group/plugin_id> [artifact]
EOF
}

is_prerelease() {
    case "$1" in
        *alpha*|*beta*|*rc*|*RC*|*SNAPSHOT*) return 0 ;;
        *) return 1 ;;
    esac
}

metadata_url() {
    local group=$1
    local artifact=${2:-}
    local group_path
    group_path=$(printf '%s' "$group" | tr '.' '/')

    if [ -z "$artifact" ]; then
        printf 'https://plugins.gradle.org/m2/%s/%s.gradle.plugin/maven-metadata.xml' "$group_path" "$group"
    else
        printf 'https://repo.maven.apache.org/maven2/%s/%s/maven-metadata.xml' "$group_path" "$artifact"
    fi
}

resolve_version() {
    local group=$1
    local artifact=${2:-}
    local url
    url=$(metadata_url "$group" "$artifact")

    local xml
    if ! xml=$(curl -fsSL "$url"); then
        echo "ERROR: Could not download metadata: $url" >&2
        return 1
    fi

    local version
    version=$(printf '%s' "$xml" | sed -n 's/.*<release>\([^<]*\)<\/release>.*/\1/p' | head -n 1)
    if [ -z "$version" ]; then
        version=$(printf '%s' "$xml" | sed -n 's/.*<latest>\([^<]*\)<\/latest>.*/\1/p' | head -n 1)
    fi

    if [ -z "$version" ]; then
        echo "ERROR: Metadata did not contain <release> or <latest>: $url" >&2
        return 1
    fi

    if is_prerelease "$version"; then
        echo "ERROR: Resolved prerelease version '$version'. Specify it explicitly if intended." >&2
        return 1
    fi

    printf '%s\n' "$version"
}

resolve_flamingock() {
    local plugin_version
    local maven_version
    plugin_version=$(resolve_version "$DEFAULT_GROUP")
    maven_version=$(resolve_version "$DEFAULT_GROUP" "$DEFAULT_ARTIFACT")

    if [ "$plugin_version" != "$maven_version" ]; then
        echo "ERROR: Flamingock plugin ($plugin_version) and Maven artifact ($maven_version) versions differ." >&2
        return 1
    fi

    echo "Flamingock: $plugin_version"
}

case "${1:-}" in
    "")
        resolve_flamingock
        ;;
    --plugin)
        echo "Gradle Plugin ($DEFAULT_GROUP): $(resolve_version "$DEFAULT_GROUP")"
        ;;
    --maven)
        echo "Maven ($DEFAULT_GROUP:$DEFAULT_ARTIFACT): $(resolve_version "$DEFAULT_GROUP" "$DEFAULT_ARTIFACT")"
        ;;
    -h|--help)
        usage
        ;;
    *)
        group=$1
        artifact=${2:-}
        if [ $# -gt 2 ]; then
            usage
            exit 1
        fi
        if [ -z "$artifact" ]; then
            echo "Gradle Plugin ($group): $(resolve_version "$group")"
        else
            echo "Maven ($group:$artifact): $(resolve_version "$group" "$artifact")"
        fi
        ;;
esac
