import os
import re

# Percorso alla root della repo Java
PROJECT_DIR = "."  # <- cambia con il percorso locale
OUTPUT_FILE = "diagram_with_dependencies.puml"

# Regex per package, classi e import
PACKAGE_RE = re.compile(r'^\s*package\s+([\w\.]+);')
CLASS_RE = re.compile(r'^\s*(public\s+)?(class|interface|enum)\s+(\w+)')
IMPORT_RE = re.compile(r'^\s*import\s+([\w\.]+);')

packages = {}        # {package_name: [class_names]}
dependencies = set() # set di (from_package, to_package)

# Scansione dei file
for root, dirs, files in os.walk(PROJECT_DIR):
    for file in files:
        if file.endswith(".java"):
            path = os.path.join(root, file)
            package_name = "default"
            class_name = None
            imported_packages = set()
            with open(path, "r", encoding="utf-8") as f:
                for line in f:
                    # Package
                    match_pkg = PACKAGE_RE.match(line)
                    if match_pkg:
                        package_name = match_pkg.group(1)
                    # Classe
                    match_cls = CLASS_RE.match(line)
                    if match_cls and not class_name:
                        class_name = match_cls.group(3)
                    # Import
                    match_imp = IMPORT_RE.match(line)
                    if match_imp:
                        imp = match_imp.group(1)
                        imp_pkg = ".".join(imp.split(".")[:-1])
                        if imp_pkg != package_name:
                            imported_packages.add(imp_pkg)

            # Salva classe nel package
            packages.setdefault(package_name, []).append(class_name)
            # Salva le dipendenze
            for imp_pkg in imported_packages:
                dependencies.add((package_name, imp_pkg))

# Genera PlantUML
with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
    f.write("@startuml\n\n")
    
    # Package con classi
    for pkg, classes in packages.items():
        f.write(f'package "{pkg}" {{\n')
        for cls in classes:
            f.write(f"    class {cls}\n")
        f.write("}\n\n")
    
    # Dipendenze tra package
    for dep_from, dep_to in dependencies:
        f.write(f'"{dep_from}" --> "{dep_to}"\n')

    f.write("@enduml\n")

print(f"File PlantUML con dipendenze generato: {OUTPUT_FILE}")
