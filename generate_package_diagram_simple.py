import os
import re

# Percorso alla root della repo Java
PROJECT_DIR = "."  # <- cambia con il percorso locale
OUTPUT_FILE = "diagram.puml"

# Regex per package e classi
PACKAGE_RE = re.compile(r'^\s*package\s+([\w\.]+);')
CLASS_RE = re.compile(r'^\s*(public\s+)?(class|interface|enum)\s+(\w+)')

packages = {}  # {package_name: [class_names]}

# Scansione dei file
for root, dirs, files in os.walk(PROJECT_DIR):
    for file in files:
        if file.endswith(".java"):
            path = os.path.join(root, file)
            package_name = "default"
            class_name = None
            with open(path, "r", encoding="utf-8") as f:
                for line in f:
                    match_pkg = PACKAGE_RE.match(line)
                    if match_pkg:
                        package_name = match_pkg.group(1)
                    match_cls = CLASS_RE.match(line)
                    if match_cls and not class_name:
                        class_name = match_cls.group(3)
                        break  # considera solo la prima classe per file
            packages.setdefault(package_name, []).append(class_name)

# Genera file PlantUML
with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
    f.write("@startuml\n\n")
    for pkg, classes in packages.items():
        f.write(f'package "{pkg}" {{\n')
        for cls in classes:
            f.write(f"    class {cls}\n")
        f.write("}\n\n")
    f.write("@enduml\n")

print(f"File PlantUML generato: {OUTPUT_FILE}")
