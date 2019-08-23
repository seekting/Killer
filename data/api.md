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

靶和人的指令一样

```json {.line-numbers}
{
  "type": "0",
  "action": "0",
  "ids": [
    "0",
    "1"
  ]
}


```

| 字段 | 值 | 含义 |
| :-----| :---- | :----: |
| type | “0" 或"1" | 靶：0,  人：1 |
| action | "0"或"1"或 "2" 或"3" | 靶：{起靶0,倒靶1,尖叫2,枪声3}  人：{死：0,复活：1,补充弹2} |
| ids| [数组] | 靶或人的id |


