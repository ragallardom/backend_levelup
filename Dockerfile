FROM ubuntu:latest
LABEL authors="ragal"

ENTRYPOINT ["top", "-b"]