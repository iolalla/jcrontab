#!/bin/bash
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
cd "$DIR"

echo "Building Jcrontab JAR and classes..."
/usr/share/maven/bin/mvn -Dmaven.repo.local="$DIR/.m2/repository" -o compile

WAR_DIR="$DIR/target/war-temp"
rm -rf "$WAR_DIR"
mkdir -p "$WAR_DIR/WEB-INF/classes" "$WAR_DIR/WEB-INF/lib"

# Copy webapp content
if [ -d "$DIR/src/main/webapp" ]; then
    cp -r "$DIR/src/main/webapp/"* "$WAR_DIR/"
fi

# Copy classes & resources
cp -r "$DIR/target/classes/"* "$WAR_DIR/WEB-INF/classes/"

# Copy runtime libs if present
SLF4J_JAR="$DIR/.m2/repository/org/slf4j/slf4j-api/2.0.12/slf4j-api-2.0.12.jar"
if [ -f "$SLF4J_JAR" ]; then
    cp "$SLF4J_JAR" "$WAR_DIR/WEB-INF/lib/"
fi

# Create WAR file
mkdir -p "$DIR/target"
jar -cf "$DIR/target/jcrontab.war" -C "$WAR_DIR" .
rm -rf "$WAR_DIR"

echo "Successfully built target/jcrontab.war"
ls -lh "$DIR/target/jcrontab.war"
