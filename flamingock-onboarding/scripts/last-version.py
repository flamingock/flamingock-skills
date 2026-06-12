import sys
import urllib.error
import urllib.request
import xml.etree.ElementTree as ET

DEFAULT_GROUP = "io.flamingock"
DEFAULT_ARTIFACT = "flamingock-community"


def metadata_url(group, artifact=""):
    group_path = group.replace(".", "/")
    if not artifact:
        return f"https://plugins.gradle.org/m2/{group_path}/{group}.gradle.plugin/maven-metadata.xml"
    return f"https://repo.maven.apache.org/maven2/{group_path}/{artifact}/maven-metadata.xml"


def is_prerelease(version):
    lowered = version.lower()
    return any(marker in lowered for marker in ("alpha", "beta", "rc", "snapshot"))


def resolve_version(group, artifact=""):
    url = metadata_url(group, artifact)
    try:
        with urllib.request.urlopen(url) as response:
            content = response.read()
    except urllib.error.URLError as exc:
        raise RuntimeError(f"Could not download metadata: {url} ({exc})") from exc

    root = ET.fromstring(content)
    version = root.findtext(".//versioning/release") or root.findtext(".//versioning/latest")
    if not version:
        raise RuntimeError(f"Metadata did not contain <release> or <latest>: {url}")
    if is_prerelease(version):
        raise RuntimeError(
            f"Resolved prerelease version '{version}'. Specify it explicitly if intended."
        )
    return version


def resolve_flamingock():
    plugin_version = resolve_version(DEFAULT_GROUP)
    maven_version = resolve_version(DEFAULT_GROUP, DEFAULT_ARTIFACT)
    if plugin_version != maven_version:
        raise RuntimeError(
            f"Flamingock plugin ({plugin_version}) and Maven artifact ({maven_version}) versions differ."
        )
    return plugin_version


def usage():
    print(
        f"""Usage:
  {sys.argv[0]}                         Resolve Flamingock from Gradle plugin and Maven metadata
  {sys.argv[0]} --plugin                Resolve the io.flamingock Gradle plugin
  {sys.argv[0]} --maven                 Resolve io.flamingock:flamingock-community
  {sys.argv[0]} <group/plugin_id> [artifact]""",
        file=sys.stderr,
    )


def main():
    args = sys.argv[1:]
    if not args:
        print(f"Flamingock: {resolve_flamingock()}")
    elif args == ["--plugin"]:
        print(f"Gradle Plugin ({DEFAULT_GROUP}): {resolve_version(DEFAULT_GROUP)}")
    elif args == ["--maven"]:
        print(f"Maven ({DEFAULT_GROUP}:{DEFAULT_ARTIFACT}): {resolve_version(DEFAULT_GROUP, DEFAULT_ARTIFACT)}")
    elif args in (["-h"], ["--help"]):
        usage()
    elif len(args) == 1:
        group = args[0]
        print(f"Gradle Plugin ({group}): {resolve_version(group)}")
    elif len(args) == 2:
        group, artifact = args
        print(f"Maven ({group}:{artifact}): {resolve_version(group, artifact)}")
    else:
        usage()
        return 1
    return 0


if __name__ == "__main__":
    try:
        sys.exit(main())
    except Exception as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        sys.exit(1)
