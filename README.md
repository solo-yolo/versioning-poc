how to run standalone jackrabbit
```bash
http -d http://apache-mirror.rbc.ru/pub/apache/jackrabbit/2.18.0/jackrabbit-standalone-2.18.0.jar
java -jar jackrabbit-standalone-2.18.0.jar --port=3333
```

create router configuration
```bash
http localhost:7070/router name=some-name

HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
Date: Mon, 01 Apr 2019 10:51:03 GMT
Transfer-Encoding: chunked

{
    "id": "459b2250-105f-499f-bf84-140dd8b6c2ec",
    "name": "some-name"
}
```

update created configuration
```bash
http put localhost:7070/router/459b2250-105f-499f-bf84-140dd8b6c2ec name=some-name description='some router configuration' interfaces:='[{"name":"eth1/1", "address":"192.168.0.1"}, {"name":"eth1/2", "address":"192.168.1.1"}]' bgp:='{"asn":123, "neighbors":[{"name":"some-neighbor", "remoteAs":321, "remoteAddress":"10.10.10.10"}]}' acl:='[{"address":"127.0.0.1","port":8080, "rule":"ACCEPT"}]'

HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
Date: Mon, 01 Apr 2019 10:53:04 GMT
Transfer-Encoding: chunked

{
    "acl": [
        {
            "address": "127.0.0.1",
            "port": 8080,
            "rule": "ACCEPT"
        }
    ],
    "bgp": {
        "asn": 123,
        "neighbors": [
            {
                "name": "some-neighbor",
                "remoteAddress": "10.10.10.10",
                "remoteAs": 321
            }
        ]
    },
    "description": "some router configuration",
    "id": "459b2250-105f-499f-bf84-140dd8b6c2ec",
    "interfaces": [
        {
            "address": "192.168.0.1",
            "name": "eth1/1"
        },
        {
            "address": "192.168.1.1",
            "name": "eth1/2"
        }
    ],
    "name": "some-name"
}
```

get versions info
```bash
http localhost:7070/router/459b2250-105f-499f-bf84-140dd8b6c2ec/versions/info

HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
Date: Mon, 01 Apr 2019 10:54:19 GMT
Transfer-Encoding: chunked

[
    {
        "labels": [
            "1"
        ],
        "name": "1.0",
        "time": "2019-04-01T10:51:03.014Z"
    },
    {
        "labels": [
            "2"
        ],
        "name": "1.1",
        "time": "2019-04-01T10:53:04.340Z"
    }
]
```

get versions
```bash
http localhost:7070/router/459b2250-105f-499f-bf84-140dd8b6c2ec/versions

HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
Date: Mon, 01 Apr 2019 10:55:00 GMT
Transfer-Encoding: chunked

[
    {
        "item": {
            "id": "459b2250-105f-499f-bf84-140dd8b6c2ec",
            "name": "some-name"
        },
        "labels": [
            "1"
        ],
        "time": "2019-04-01T10:51:03.014Z",
        "version": "1.0"
    },
    {
        "item": {
            "acl": [
                {
                    "address": "127.0.0.1",
                    "port": 8080,
                    "rule": "ACCEPT"
                }
            ],
            "bgp": {
                "asn": 123,
                "neighbors": [
                    {
                        "name": "some-neighbor",
                        "remoteAddress": "10.10.10.10",
                        "remoteAs": 321
                    }
                ]
            },
            "description": "some router configuration",
            "id": "459b2250-105f-499f-bf84-140dd8b6c2ec",
            "interfaces": [
                {
                    "address": "192.168.0.1",
                    "name": "eth1/1"
                },
                {
                    "address": "192.168.1.1",
                    "name": "eth1/2"
                }
            ],
            "name": "some-name"
        },
        "labels": [
            "2"
        ],
        "time": "2019-04-01T10:53:04.340Z",
        "version": "1.1"
    }
]
```
