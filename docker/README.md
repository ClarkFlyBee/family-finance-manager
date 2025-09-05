项目使用 Docker 部署环境

- 启动项目（在**根目录**下执行）

```
docker compose up --build
```

- 如果项目内容有修改，需要删除容器后再次启动项目
- 删除容器及挂载卷

```
docker compose down -v
```

- 进入容器使用命令行操作数据库
- **注意** 项目启动后，需要稍等一会儿再进入容器

```
docker exec -it <容器名> mysql -u<用户名> -p<密码> -D<数据库名> -e "<SQL语句>"
```

```
docker exec -it family-finance-manager-db-1 mysql -uroot -proot
```