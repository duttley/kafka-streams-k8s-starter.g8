FROM mozilla/sbt:8u232_1.3.8 AS build-env
COPY credentials /root/.sbt/.credentials
ADD . /app
WORKDIR /app
RUN sbt 'set test in assembly := {}' clean assembly
RUN mkdir -p /out && mv target/scala-2.13/$name;format="normalize"$-assembly.jar /out

FROM gcr.io/distroless/java:11
COPY --from=build-env /out /app
WORKDIR /app
CMD ["$name;format="normalize"$-assembly.jar"]
