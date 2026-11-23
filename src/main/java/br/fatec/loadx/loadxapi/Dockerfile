# ============================================================
# Dockerfile
# ------------------------------------------------------------
# Receita para empacotar o LoadX em um CONTAINER, que é o formato
# aceito pelo serviço de hospedagem (Render). Tem duas fases:
#   1) BUILD: usa o JDK + Maven Wrapper (mvnw) para compilar e gerar
#      o arquivo .jar da aplicação.
#   2) EXECUCAO: copia só o .jar para uma imagem menor (apenas JRE),
#      que é a que roda no servidor.
# ============================================================

# ---------- Fase 1: compilar ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copia primeiro só o necessário para baixar as dependências
# (assim o Docker reaproveita essa etapa quando só o código muda).
COPY .mvn .mvn
COPY mvnw pom.xml ./
# Remove quebras de linha do Windows (CRLF), que quebram o script no Linux.
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw -q dependency:go-offline

# Agora o código e a geração do .jar (sem rodar testes).
COPY src src
RUN ./mvnw -q -DskipTests package

# ---------- Fase 2: executar ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# O plano gratuito tem 512 MB de memória: limita o heap do Java
# para sobrar espaço para o restante do processo.
ENV JAVA_TOOL_OPTIONS="-Xmx320m"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
