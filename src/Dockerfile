
# Stage 1: Build
FROM gradle:7.5.1-jdk17-alpine AS build

# Créez un répertoire SetterBackend dans le conteneur
RUN mkdir /SetterBackend

# Définissez le répertoire de travail dans le conteneur
WORKDIR /SetterBackend

# Copiez les fichiers de configuration Gradle depuis la racine du projet
COPY ./build.gradle ./settings.gradle /SetterBackend/

# Copiez le code source de l'application
COPY . /SetterBackend/

# Exécutez la tâche Gradle pour construire l'application
#RUN gradle clean build
RUN  /bin/sh /SetterBackend/gradlew

# Stage 2: Package
FROM openjdk:17-oracle

# Définissez le répertoire de travail dans le conteneur
WORKDIR /app

# Copiez les artefacts construits depuis le premier stage
COPY --from=build /SetterBackend/build/libs/setter-0.0.1-SNAPSHOT.jar /app/setter.jar

# Exposez le port sur lequel votre application s'exécutera
EXPOSE 8595

# Commande à exécuter lorsque le conteneur démarre
ENTRYPOINT ["java", "-jar", "/app/setter.jar"]


#RUN  /bin/sh /SetterBackend/gradlew