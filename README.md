## გაშვება

აპლიკაციის გასაშვებად თავდაპირველად საჭიროა გაუშვათ docker-compose.yaml ფაილი, შემდეგ აპლიკაცია
მზადაა გასაშვებად

```bash
docker-compose up
```

---

## ბაზა

ბაზაში არის ორი ცხრილი, რექუესთებისა და ამანათების. რექუესთების ცხრილი ინახავს შემდეგ პარამეტრებს:

* id
* total_volume
* total_revenue
* max_volume
* created_at
* load_status

ამანათების ცხრილი:

* id,
* request_id (ებმის რექუესთების ცხრილს),
* name
* volume
* revenue
* is_selected

ამანათების ცხრილის ველებს - _request_id_ და _is_selected_ დადებული აქვს ინდექსი ამანათების სწრაფი ძიებისთვის.

---

## მეთოდები

### 1. /load

რექუესთის შექმნა და ოპტიმიზაციის დაწყება:

```bash
curl -X POST http://localhost:8080/api/v1/van/load \
-H "Content-Type: application/json" \
-d '{
  "maxVolume": 15,
  "availableShipments": [
    {
      "name": "Parcel A",
      "volume": 5,
      "revenue": 120
    },
    {
      "name": "Parcel B",
      "volume": 10,
      "revenue": 200
    },
    {
      "name": "Parcel C",
      "volume": 3,
      "revenue": 80
    },
    {
      "name": "Parcel D",
      "volume": 8,
      "revenue": 160
    }
  ]
}'
```

აბრუნებს request_id და load_status პარამეტრებს.
load_status-ს აქვს 3 მნიშვნელობა:

* CREATED (შეიქმნა და ჩაიწერა ბაზაში)
* LOADING (მიმდინარეობს კალკულაციები საუკეთესო მოგების დასათვლელად)
* LOADED (კალკულაცია დასრულებულია)

```json
{
  "requestId": "9dd673d1-e6db-40c9-bf65-9c6b226563b6",
  "loadStatus": "CREATED"
}
```

---

### 2. /request/{id}

რექუესთის სტატუსის და არჩეული ამანათების მიღება:

```bash
curl -X GET http://localhost:8080/api/v1/van/request/9dd673d1-e6db-40c9-bf65-9c6b226563b6
```

რესპონსი:

```json
{
  "requestId": "9dd673d1-e6db-40c9-bf65-9c6b226563b6",
  "selectedShipments": [
    {
      "name": "Parcel A",
      "revenue": 120,
      "volume": 5
    },
    {
      "name": "Parcel B",
      "revenue": 200,
      "volume": 10
    }
  ],
  "totalVolume": 15,
  "totalRevenue": 320,
  "createdAt": "2026-05-10T19:17:49.992251Z",
  "loadStatus": "LOADED"
}
```

---

### 3. /filter

#### მხოლოდ სტატუსით ფილტრაცია

```bash
curl -X GET "http://localhost:8080/api/v1/van/filter?status=LOADED"
```

#### მინიმალური და მაქსიმალური მოგებით ფილტრაცია

```bash
curl -X GET "http://localhost:8080/api/v1/van/filter?minRevenue=100&maxRevenue=500"
```

#### დროის ინტერვალით ფილტრაცია

```bash
curl -X GET "http://localhost:8080/api/v1/van/filter?from=2026-04-01T00:00:00Z&to=2026-06-30T23:59:59Z"
```

#### რამდენიმე პარამეტრის ერთად გამოყენება

```bash
curl -X GET "http://localhost:8080/api/v1/van/filter?status=LOADED&minRevenue=200&from=2026-04-01T00:00:00Z"
```

რესპონსი:

```json
[
  {
    "requestId": "9dd673d1-e6db-40c9-bf65-9c6b226563b6",
    "selectedShipments": [
      {
        "name": "Parcel A",
        "revenue": 120,
        "volume": 5
      },
      {
        "name": "Parcel B",
        "revenue": 200,
        "volume": 10
      }
    ],
    "totalVolume": 15,
    "totalRevenue": 320,
    "createdAt": "2026-05-10T19:17:49.992251Z",
    "loadStatus": "LOADED"
  },
  {
    "requestId": "f5df0304-8eb4-454a-91f8-8266938a0962",
    "selectedShipments": [
      {
        "name": "Parcel C",
        "revenue": 80,
        "volume": 3
      },
      {
        "name": "Parcel D",
        "revenue": 160,
        "volume": 8
      }
    ],
    "totalVolume": 11,
    "totalRevenue": 240,
    "createdAt": "2026-05-11T13:05:12.843813Z",
    "loadStatus": "LOADED"
  }
]
```