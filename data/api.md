# 接口

以下接口通过socket完成，编码格式为utf-8

## 服务器推送

### 获取靶列表

type=0表示靶
```json {.line-numbers}
{
  "type": "0",
  "bars": [
    {
      "id": "1",
      "name": "bar 1"
    },
    {
      "id": "2",
      "name": "bar 2"
    },
    {
      "id": "3",
      "name": "bar 3"
    }
  ]
}

```
### 获取人列表

type=1表示人
```json {.line-numbers}
{
  "type": "1",
  "persons": [
    {
      "id": "1",
      "name": "person 1"
    },
    {
      "id": "2",
      "name": "person 2"
    },
    {
      "id": "3",
      "name": "person 3"
    }
  ]
}

```

## 客户端发起控制指令

客户端发起控制指令

### 靶指令

```json
{
  "type": "0",//起靶0，倒靶1
  "bar": "1"//靶的id
}
```

### 人指令


```json
{
  "type": "0",//出局0，复活1
  "person": "1"//人的id
}
```
## 数据上传

ftp上传
ip:socket地址
端口：21
用户名:ftp,密码ftp
需要在ftp根目录创建
ftp/audio  ftp/img  ftp/location  ftp/video


