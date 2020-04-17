# Notes Example Project
This project serves as an example
assignment at Hogeschool Utrecht
for working with GitHub Actions
as build pipelines.

We will package a jar 
using GitHub Actions
as a build pipeline
and GitHub Packages 
as an artifact repository.

Tip: look at the `end-result` 
branch for the end result. Pipelines and all.

## 1. Docker Compose
We use docker-compose for our development
setup: a docker container for postgres.

Make sure docker is installed 
and configured correctly (including
network and volumes)

Run the database:
```bash
docker-compose up db
```

## 2. Make a JAR with Maven
Look at `pom.xml` for our dependencies
and lifecycle management using maven.
In order to build a `.jar` for our project,
execute the following command:
```bash
mvn clean package
```

This cleans the `target` directory
and builds 
`notes-example-build-1.0-SNAPSHOT.jar`.

To run this in java execute the following:
```bash
jar -jar target/notes-example-build-1.0-SNAPSHOT.jar
```

Make sure the database is still running
using docker-compose.

## 3. Try the application
Use curl or postman to try the application:
```bash
curl --location --request POST 'localhost:8080/notes' \
    --header ': ' \
    --header 'Content-Type: application/json' \
    --data-raw '{
	    "title": "My first post",
	    "author": "Alex",
	    "contents": "Hello world!"
    }'

curl --location --request GET 'localhost:8080/notes' \
    --header ': ' \
    --header 'Content-Type: application/json'
```

## 4. Dockerize the application
Create a `Dockerfile` to define the
steps to build an image:
```dockerfile
# Select a base image
FROM adoptopenjdk/openjdk13:alpine-jre

# Set working directory
WORKDIR /usr/app

# Copy target jars into the working directory
COPY target/*.jar app.jar

# Set the command to run upon start
ENTRYPOINT ["java","-jar","app.jar"]
```

Build the container:
```bash
docker build -t notes-example .
```

Run the container:
```bash
docker run -p 80:8080 -e DATABASE_URL=postgres://dev:dev@host.docker.internal:54321/notebook 
notes-example
```

Note that `host.docker.internal` refers to the `localhost` 
on the host machine. This does not work on every machine!
See: https://docs.docker.com/docker-for-windows/networking/#known-limitations-use-cases-and-workarounds

In practice, it is better to link containers up manually
instead of through the host system. This is one of
the reasons to use container management systems
like Docker Compose, Docker Swarm or Kubernetes.

## 5. Distributable JAR (pom.xml)
Edit the `pom.xml` to define
a distribution repository for
your `.jar` file, to be used
when running `mvn deploy`:
```xml
<distributionManagement>
  <repository>
    <id>github</id>
    <name>project-name</name>
    <url>https://maven.pkg.github.com/github-user/github-repo-name</url>
  </repository>
</distributionManagement>
```

Change `project-name`, 
`github-username` and `github-repo` 
with the values of your repository.

For a workflow, it would be nice
to configure this using environment
variables:
```xml
<distributionManagement>
  <repository>
    <id>${env.DISTRIBUTION_ID}</id>
    <name>${env.DISTRIBUTION_NAME}</name>
    <url>${env.DISTRIBUTION_URL}</url> 
  </repository>
</distributionManagement>
```

### 6. Create workflow, jobs and steps
We'll create a workflow
with 3 jobs: `build`,
`package-jar`, `package-image`.

The jobs `package-jar` and `package-image`
can run in parallel after the `build`:
1. The `build` job wil create a `.jar` and 
saves it as an artifact. 
1. The `package-jar` job
will load the artifact and package and publish
the `.jar` as a GitHub Package for this repository.
1. The `package-image` job
will load the artifact, create a docker image
of the `.jar` and publishes it as a GitHub Package
for this repository.

An example of how to do it
(thorough explanation in the assignment):
```yml
name: Build pipeline

# When should this pipeline run?
# on pushes to master and PRs to master
# Note that you may not want to build packages on pull requests!
# You could also use on: release: ..., for deploying upon tagging a release.
on:
  push:
    branches: [ master ]
  pull_request:
    branches: [ master ]

# What jobs does this workflow consist of?
jobs:
  # The job called "build"
  build:
    # Use ubuntu as an image for our build environment
    runs-on: ubuntu-latest

    # What steps does this job consist of?
    steps:
    - uses: actions/checkout@v2

    # Setup version 12 of Java
    - name: Set up JDK 12
      uses: actions/setup-java@v1
      with:
        java-version: 12

    # Cache maven dependencies for each build
    - uses: actions/cache@v1
      with:
        path: ~/.m2/repository
        key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
        restore-keys: |
          ${{ runner.os }}-maven-

    # Build our maven target (runs all tests etc)
    - name: Build using Maven
      run: mvn clean package

    # Save the contents of the "target" directory
    # So we can use it in another job as "build-result"
    - name: Save result
      uses: actions/upload-artifact@v1
      with:
        name: build-result
        path: target

  # The job called "package-jar"
  package-jar:
    # This job depends on the "build" job
    needs: build

    # Use ubuntu as an image for our build environment
    runs-on: ubuntu-latest

    # What steps does this job consist of?
    steps:
      # Checkout the code
      - uses: actions/checkout@v2

      # Load the result called "build-result"
      # And make it available in the "target" directory
      - name: Load result
        uses: actions/download-artifact@v1
        with:
          name: build-result
          path: target

      # Setup version 12 of Java
      - name: Set up JDK 12
        uses: actions/setup-java@v1
        with:
          java-version: 12

      # Run all maven phases to deployment
      # with our repository as a target
      - name: Publish jar to GitHub Packages
        run: mvn deploy
        env:
          # Authenticate with GitHub using
          # token from the secrets context
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          # Add distribution env vars
          # used in pom.xml
          DISTRIBUTION_ID: github
          DISTRIBUTION_NAME: Notes Example Project
          DISTRIBUTION_URL: https://maven.pkg.github.com/${{ github.repository }}

  # The job called "package-image"
  package-image:
      # This job depends on the "build" job
      needs: build

      # Use ubuntu as an image for our build environment
      runs-on: ubuntu-latest

      # What steps does this job consist of?
      steps:
      # Checkout the code
      - uses: actions/checkout@v2

      # Load the result called "build-result"
      # And make it available in the "target" directory
      - name: Load result
        uses: actions/download-artifact@v1
        with:
          name: build-result
          path: target

      # Run a few (bash) commands to
      # build an publish an image
      - name: Build and publish Docker image to GitHub Packages
        env:
          DOCKER_IMAGE_TARGET: docker.pkg.github.com/${{ github.repository }}/notes-example
          GITHUB_PACKAGE_REGISTRY_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        # 1. Build a docker image using the specified target as a name
        # 2. Login to GitHub docker registry by piping the token into the password prompt
        # 3. Push the image to the registry
        run: |
          docker build --tag ${DOCKER_IMAGE_TARGET} .
          echo "${GITHUB_PACKAGE_REGISTRY_TOKEN}" | docker login docker.pkg.github.com -u ${{ github.actor }} --password-stdin
          docker push ${DOCKER_IMAGE_TARGET}
```
