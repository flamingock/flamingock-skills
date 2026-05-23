#!/bin/bash

# --- Obtiene la versión desde el archivo maven-metadata.xml del repositorio. ---
get_latest_from_metadata() {
    local group=$1
    local artifact=$2
    # Convertir puntos en barras para la URL del repo
    local group_path=$(echo "$group" | tr '.' '/')

    if [ -z "$artifact" ]; then
        local url="https://plugins.gradle.org/m2/$group_path/$group.gradle.plugin/maven-metadata.xml"
        local label="Gradle Plugin ($group)"
    else
        local url="https://repo1.maven.org/maven2/$group_path/$artifact/maven-metadata.xml"
        local label="Maven ($group:$artifact)"
    fi

    # Descargar XML y extraer el valor de <release> o <latest>
    local version=$(curl -s "$url" | sed -n 's/.*<release>\(.*\)<\/release>.*/\1/p')

    # Si <release> está vacío, intentar con <latest>
    if [ -z "$version" ]; then
        version=$(curl -s "$url" | sed -n 's/.*<latest>\(.*\)<\/latest>.*/\1/p')
    fi

    if [ -n "$version" ]; then
        echo "$label: $version"
    else
        echo "$label: No encontrado"
    fi
}

# --- Lógica de parámetros ---
if [ -z "$1" ]; then
    echo "Uso: bash $0 <group/plugin_id> [artifact]"
    exit 1
fi

get_latest_from_metadata "$1" "$2"