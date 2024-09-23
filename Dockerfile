FROM ubuntu:latest
LABEL authors="divjazz"

ENTRYPOINT ["top", "-b"]