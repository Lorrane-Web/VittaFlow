# VittaFlow MVP

Projeto acadêmico Java 25 + Spring Boot + JSF/PrimeFaces + JPA/Hibernate + MySQL.

## Executar

1. Abra a pasta no VSCode.
2. Verifique Java 25: `java -version`
3. Verifique Maven: `mvn -version`
4. No PowerShell, configure a variável da senha do MySQL na mesma janela em que iniciará o app:
   ```powershell
   $env:VITTAFLOW_DB_PASSWORD = Read-Host "Senha do MySQL"
   ```
5. Execute nessa mesma janela: `mvn spring-boot:run`
6. Abra: <http://localhost:8080/login.xhtml>

O banco utilizado é o MySQL `vittaflow`. As tabelas são criadas/atualizadas pelo Hibernate e permanecem vazias até que o usuário cadastre ou preencha dados pelo site.

## Novas funcionalidades do acompanhamento profissional

- Dashboard separado para o profissional.
- Lista de usuários vinculados/aceitos.
- Consultas online demonstrativas.
- Acompanhamento de água, passos, sono, exercícios e gráficos.
- Dados do usuário em modo somente leitura para o profissional.
- Aba de feedback para registrar orientações.
- Usuário consulta os profissionais selecionados e os feedbacks recebidos.
- Requisitos detalhados em `REQUISITOS.md`.
