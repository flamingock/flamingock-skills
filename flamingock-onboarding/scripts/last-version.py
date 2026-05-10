import urllib.request
import xml.etree.ElementTree as ET
import sys

def get_latest_from_metadata(group, artifact):
    """Obtiene la versión desde el archivo maven-metadata.xml del repositorio."""

    group_path = group.replace('.', '/')
    if not artifact:
        url = f"https://plugins.gradle.org/m2/{group_path}/{group}.gradle.plugin/maven-metadata.xml"
        label = f"Gradle Plugin ({group})"
    else:
        url = f"https://repo1.maven.org/maven2/{group_path}/{artifact}/maven-metadata.xml"
        label = f"Maven ({group}:{artifact})"

    try:
        # Realizamos la petición
        with urllib.request.urlopen(url) as response:
            content = response.read()
            root = ET.fromstring(content)
            # Buscamos <release> o <latest>
            version = root.findtext(".//versioning/release") or root.findtext(".//versioning/latest")
            return f"{label}: {version}" if version else f"{label}: Versión no encontrada"

    except Exception as e:
        return f"{label}: No encontrado"

if __name__ == "__main__":
    if not sys.argv[1:]:
        print(f"Uso: python3 {sys.argv[0]} <group/plugin_id> [artifact]")
        exit(1)
    print(get_latest_from_metadata(sys.argv[1], sys.argv[2] if len(sys.argv[1:]) > 1 else ""))