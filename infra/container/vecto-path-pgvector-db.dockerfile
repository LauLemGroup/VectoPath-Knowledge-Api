FROM postgres:17

RUN apt-get update && \
    apt-get install -y postgresql-server-dev-17 build-essential git && \
    git clone --depth 1 https://github.com/pgvector/pgvector.git /pgvector && \
    cd /pgvector && make && make install && \
    rm -rf /pgvector && \
    apt-get remove --purge -y git build-essential && \
    apt-get autoremove -y && apt-get clean
