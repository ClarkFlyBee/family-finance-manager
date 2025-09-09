数据库表结构

### 用户表（User）

| 字段名称 | 字段类型 | 空 | 默认值 | 约束 | 主/外键 | 索引 | 描述信息 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| id | BIGINT |  | AUTO_INCREMENT | PRIMARY KEY | PK | PRIMARY | 用户唯一标识符 |
| family_id | BIGINT |  |  | FOREIGN KEY | FK | KEY fk_family | 所属家庭编号 |
| name | VARCHAR(30) |  |  |  |  |  | 姓名 |
| sex | CHAR(1) |  |  | CHECK(sex IN ('M', 'F')) |  |  | 性别（M 男，F 女） |
| phone | VARCHAR(20) |  |  |  |  | KEY idx_phone | 手机号 |
| password | CHAR(60) |  |  |  |  |  | 密码散列 |
| role | CHAR(1) |  | 'U' | CHECK(sex IN ('A', 'U')) |  |  | 角色（A 管理员，U 普通） |
| created_at | DATETIME(0) |  | CURRENT_TIMESTAMP |  |  |  | 创建时间 |
| updated_at | DATETIME(0) |  | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |  |  |  | 更新时间 |

### 家庭表（Family）

| 字段名称 | 字段类型 | 空 | 默认值 | 约束 | 主/外键 | 索引 | 描述信息 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| id | BIGINT |  | AUTO_INCREMENT | PRIMARY KEY | PK | PRIMARY | 家庭唯一标识符 |
| family_name | VARCHAR(50) |  |  |  |  |  | 家庭名称 |
| created_at | DATETIME(0) |  | CURRENT_TIMESTAMP |  |  |  | 创建时间 |
| updated_at | DATETIME(0) |  | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |  |  |  | 最后修改时间 |

### 收入表（Income）

| 字段名称 | 字段类型 | 空 | 默认值 | 约束 | 主/外键 | 索引 | 描述信息 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| id | BIGINT |  | AUTO_INCREMENT | PRIMARY KEY | PK | PRIMARY | 收入记录唯一标识符 |
| inc_no | VARCHAR(20) |  |  | UNIQUE |  | KEY idx_inc_no | 业务编号（INyyyymmddNNNN） |
| owner_type | CHAR(1) |  |  | CHECK(owner_type IN ('F', 'M')) |  |  | 所属成员类型（F 家庭，M 个人） |
| owner_id | BIGINT |  |  |  | FK | KEY idx_owner_id | 家庭ID 或 个人ID（是外键，但无法创建外键约束，需要通过程序保证一致性） |
| category_id | BIGINT |  |  | FOREIGN KEY | FK | KEY idx_cate | 收入分类ID |
| amount | DECIMAL(12,2) |  |  | CHECK(amount > 0) |  |  | 金额（元） |
| inc_time | DATETIME(0) |  |  |  |  | KEY idx_inc_time | 收入时间 |
| remark | VARCHAR(500) | √ |  |  |  |  | 备注 |
| is_draft | TINYINT(1) |  | 0 |  |  |  | 是否草稿（1 是，0 否） |
| created_at | DATETIME(0) |  | CURRENT_TIMESTAMP |  |  |  | 创建时间 |
| updated_at | DATETIME(0) |  | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |  |  |  | 最后修改时间 |

### 支出表（Expense）

| 字段名称 | 字段类型 | 空 | 默认值 | 约束 | 主/外键 | 索引 | 描述信息 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| id | BIGINT |  | AUTO_INCREMENT | PRIMARY KEY | PK | PRIMARY | 支出记录唯一标识符 |
| exp_no | VARCHAR(20) |  |  | UNIQUE |  | KEY idx_exp_no | 业务编号（EXyyyymmddNNNN） |
| owner_type | CHAR(1) |  |  | CHECK(owner_type IN ('F', 'M')) |  |  | 所属成员类型（F 家庭，M 个人） |
| owner_id | BIGINT |  |  |  | FK | KEY idx_owner_id | 家庭ID 或 个人ID（是外键，但无法创建外键约束，需要通过程序保证一致性） |
| category_id | BIGINT |  |  | FOREIGN KEY | FK | KEY idx_cate | 支出分类ID |
| amount | DECIMAL(12,2) |  |  | CHECK(amount > 0) |  |  | 金额（元） |
| exp_time | DATETIME(0) |  |  |  |  | KEY idx_exp_time | 支出时间 |
| remark | VARCHAR(500) | √ |  |  |  |  | 备注 |
| is_draft | TINYINT(1) |  | 0 |  |  |  | 是否草稿（1 是，0 否） |
| created_at | DATETIME(0) |  | CURRENT_TIMESTAMP |  |  |  | 创建时间 |
| updated_at | DATETIME(0) |  | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |  |  |  | 最后修改时间 |

### 分类（Category）

| 字段名称 | 字段类型 | 空 | 默认值 | 约束 | 主/外键 | 索引 | 描述信息 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| id | BIGINT |  | AUTO_INCREMENT | PRIMARY KEY | PK | PRIMARY | 分类记录唯一标识符 |
| parent_id | BIGINT |  | 0 |  |  | KEY idx_parent | 父级分类ID，0=顶级 |
| type | CHAR(1) |  |  | CHECK (type IN ('l', 'E')) |  | KEY idx_type | 类型（I 收入，E 支出） |
| name | VARCHAR(50) |  |  |  |  |  | 分类名称 |
| is_system | TINYINT(1) |  | 0 |  |  |  | 是否系统内置（1 是，0 否） |
| created_by | BIGINT |  |  | FOREIGN KEY | FK | KEY fk_user | 创建者ID |
| visibility | CHAR(1) |  | 'F' | CHECK (visibility IN ('F', 'P')) |  |  | 可见范围：'F' 全家可见 'P' 仅个人可见 |
| created_at | DATETIME(0) |  | CURRENT_TIMESTAMP |  |  |  | 创建时间 |
| updated_at | DATETIME(0) |  | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |  |  |  | 最后修改时间 |