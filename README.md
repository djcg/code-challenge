# Hospital

The purpose of this project is to create a simple backend that mimics a private hospital.

## What is in it?

* A Spring Boot REST API for creating patients, doctors and appointments.
* A Postgres DB to store said patients, doctors and appointments.

## How to run

### Requirements

* Java 11
* Maven
* Docker

### Steps

You simply have to run the following commands:
```
mvn clean package // to generate the jar
docker build .
docker-compose up -d
```

For convenience, there is also a file named *build_and_start.sh* that does exactly what the previous commands do

This will start both the application and the Postgres DB. The application will run on port 8080.

### First run

On the first run, an **admin** user will be created with the following credentials

```
admin/admin
```

This user is needed for creating the doctors on the hospital.

### Is it running?

You can check by running the following command

```
curl -X GET 'localhost:8080/private-hospital/health'
```

If everything is ok, then you get a 200 OK response with the health response payload.

## Relevant endpoints

### Base URL
```
http://localhost:8080/private-hospital
```

### User Management

### Unauthenticated endpoints

There are two endpoints that don't need authentication to be interacted with: the **register patient** and the **login**


#### Register Patient

A patient can register himself, doesn't need any roles to do it. It simply has to invoke

```
curl --request POST \
  --url http://localhost:8080/private-hospital/auth/register \
  --header 'Content-Type: application/json' \
  --data '{
	"name": "A_Name",
	"username": "A_Username",
	"password": "A_Password",
	"symptoms": "A brief description of symptoms"
}'
```

and the HTTP response will be 201 with payload:

```
{
  "id": "24deed33-f44e-4b1f-ba63-b8ab0a9711ac"
}
```
where the id will be the id of the newly created patient

#### Login

An user can login (whether a doctor, a patient or an admin):

```
curl --request POST \
  --url http://localhost:8080/private-hospital/auth/login \
  --header 'Content-Type: application/json' \
  --data '{
	"username": "username_to_login",
	"password": "respective_password"
}'
```

and the HTTP response will be 200 with payload:

```
{
  "token": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwc2QiLCJleHAiOjE2MTcxMzc1OTJ9.jT2X0UJTCuDUKGH_Jy3ocC-9uzYz68qWzdAP5i1ZNBo",
  "id": "24deed33-f44e-4b1f-ba63-b8ab0a9711ac",
  "name": "name_of_user",
  "username": "username_of_user",
  "roles": [
    "ROLE_PATIENT"
  ],
  "type": "Bearer"
}
```

And the **token** field will be used as an Authorization header for all subsequent requests.

### Authenticated endpoints

On all authenticated endpoints the user needs to send the jwt token obtained on the login request.

#### Create doctor 

This endpoint can only be invoked by an admin.

```
curl --request POST \
  --url http://localhost:8080/private-hospital/v1/doctors/ \
  --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYxNzA1NzIxMn0.-sZQhlmDir0JicY97p1GILLvqOSq_N1HB4GfIQml1JQ' \
  --header 'Content-Type: application/json' \
  --data '{
	"name": "Doctor_name",
	"username": "doctor_username",
	"specialty": "A free text field",
	"password": "doctor_password"
}'
```

This will return 201 and the following payload

```
{
    "id": "24deed33-f44e-4b1f-ba63-b8ab0a9711ac"
}
```

#### Get Page of Doctors

Any user can access this endpoint. This lists all doctors that are registered on the hospital. There are also two query parameters that specify how the pagination will be done. These will be:

* page (default = 1) (Minimum value = 1)
* page_size (default = 10) (Minimum value = 5)

```
curl --request GET \
  --url http://localhost:8080/private-hospital/v1/doctors \
  --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYxNzEzOTcyNH0.PfNAN-9hjJmNjb2xjaxBUrTYEYTz_c37RJ56TCfiae4' \
  --header 'Content-Type: application/json'
```

And the response will be 200 with the payload

```
{
  "content": [
    {
      "id": "77c9e6e6-7716-4a8e-af5b-f69a7db07c74",
      "name": "doctor1",
      "specialty": "Cardiology"
    },
    {
      "id": "bca50610-5b27-433b-a02b-2a44be16565a",
      "name": "doctor2",
      "specialty": "Cardiology"
    },
    {
      "id": "f6ca5945-e76f-41d4-9668-33dc669cbdee",
      "name": "doctor3",
      "specialty": "Cardiology"
    },
    {
      "id": "b8684094-3f22-4273-bd85-6e2ac1308d43",
      "name": "doctor4",
      "specialty": "Cardiology"
    },
    {
      "id": "8df18374-8292-4a41-86d2-44a317daf93b",
      "name": "doctor5",
      "specialty": "Cardiology"
    },
    {
      "id": "1f4881fd-672a-41a0-b0f7-0aa8b50c40cd",
      "name": "doctor6",
      "specialty": "Cardiology"
    },
    {
      "id": "2d6f81bd-c5e9-45a0-a311-71bbe67eb0ef",
      "name": "doctor7",
      "specialty": "Cardiology"
    },
    {
      "id": "21f20d87-5ae5-43ef-acf6-31d54608681c",
      "name": "doctor8",
      "specialty": "Cardiology"
    },
    {
      "id": "42aab8c4-ea35-4568-a34d-72b5c225159d",
      "name": "doctor9",
      "specialty": "Cardiology"
    },
    {
      "id": "cf13d2fa-978e-44c7-8f3a-0d8de0aae0e0",
      "name": "doctor10",
      "specialty": "Cardiology"
    }
  ],
  "pageable": {
    "sort": {
      "sorted": false,
      "unsorted": true,
      "empty": true
    },
    "pageNumber": 0,
    "pageSize": 10,
    "offset": 0,
    "unpaged": false,
    "paged": true
  },
  "last": false,
  "totalPages": 2,
  "totalElements": 11,
  "numberOfElements": 10,
  "sort": {
    "sorted": false,
    "unsorted": true,
    "empty": true
  },
  "first": true,
  "size": 10,
  "number": 0,
  "empty": false
}
```

#### Create Appointments

In order to create an appointment with a specific doctor, a patient can perform the following request (please be mindful of the doctorId on the url):

```
curl --request POST \
  --url http://localhost:8080/private-hospital/v1/doctors/{doctorId}/appointments \
  --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwYXRpZW50MiIsImV4cCI6MTYxNzE0MDY3M30.IkLUfJ-U0beGmZxJ7uYbRA8Fnf0YpofBgNgV1Bjk9RY' \
  --header 'Content-Type: application/json' \
  --data '{
	"appointment_date": "2021-04-04T15:00:00Z"
}'
```

This will return 201 and the following payload

```
{
    "id": "24deed33-f44e-4b1f-ba63-b8ab0a9711ac"
}
```

#### Get Appointments

As a doctor, the user can get a list of his appointments, paginated. Same as before, there are the following query parameters:

* page (default = 1) (Minimum value = 1)
* page_size (default = 10) (Minimum value = 5)

```
curl --request GET \
  --url 'http://localhost:8080/private-hospital/v1/doctors/appointments' \
  --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkb2N0b3I1IiwiZXhwIjoxNjE3MTQwNTc1fQ.meK6mnyTYXWy1G6ibM4W6SBw2ZWU7hnTqm8osJYunls' \
  --header 'Content-Type: application/json'
```

And the response will be a 200 with the following payload:

```
{
  "content": [
    {
      "doctor_name": "doctor5",
      "patient_name": "name1",
      "specialty": "Cardiology",
      "start_date": "2021-04-01T09:00:00Z",
      "end_date": "2021-04-01T10:00:00Z"
    },
    {
      "doctor_name": "doctor5",
      "patient_name": "name1",
      "specialty": "Cardiology",
      "start_date": "2021-04-01T10:00:00Z",
      "end_date": "2021-04-01T11:00:00Z"
    },
    {
      "doctor_name": "doctor5",
      "patient_name": "name1",
      "specialty": "Cardiology",
      "start_date": "2021-04-01T11:00:00Z",
      "end_date": "2021-04-01T12:00:00Z"
    },
    {
      "doctor_name": "doctor5",
      "patient_name": "name1",
      "specialty": "Cardiology",
      "start_date": "2021-04-02T11:00:00Z",
      "end_date": "2021-04-02T12:00:00Z"
    },
    {
      "doctor_name": "doctor5",
      "patient_name": "name2",
      "specialty": "Cardiology",
      "start_date": "2021-04-04T15:00:00Z",
      "end_date": "2021-04-04T16:00:00Z"
    }
  ],
  "pageable": {
    "sort": {
      "sorted": false,
      "unsorted": true,
      "empty": true
    },
    "pageNumber": 0,
    "pageSize": 10,
    "offset": 0,
    "unpaged": false,
    "paged": true
  },
  "last": true,
  "totalPages": 1,
  "totalElements": 5,
  "numberOfElements": 5,
  "sort": {
    "sorted": false,
    "unsorted": true,
    "empty": true
  },
  "first": true,
  "size": 10,
  "number": 0,
  "empty": false
}
```

#### Schedule leave

As a doctor, a user can schedule a period of time that he will be unavailable.

```
curl --request POST \
--url http://localhost:8080/private-hospital/v1/doctors/schedule-leave \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkb2N0b3I1IiwiZXhwIjoxNjE3MTQ1NjEzfQ.3hMo7jIi0YBanBFZjmjv6fQ75dBhU8vZ9nqwgjhr3Z4' \
--header 'Content-Type: application/json' \
--data '{
"start_date" : "2021-04-02T09:00:00Z",
"end_date" : "2021-04-02T11:00:00Z",
"leave_type" : "Sick"
}'
```

This will return 201 and the following payload

```
{
    "id": "24deed33-f44e-4b1f-ba63-b8ab0a9711ac"
}
```

#### Check doctor availability

As a patient a user can check a doctor availability for the following seven days.

```
curl --request GET \
  --url http://localhost:8080/private-hospital/v1/doctors/{doctorId}/appointments \
  --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwYXRpZW50MiIsImV4cCI6MTYxNzE0NTEyMn0.LRzfG9tXRg0dO1XVLBhLgI_pLjFY_VHoONfih3vbhGo' \
  --header 'Content-Type: application/json'
```

This will return a 200 status code and the following payload

```
{
  "available_time_slots": [
    {
      "start_date": "2021-03-31T09:00:00Z",
      "end_date": "2021-03-31T10:00:00Z"
    },
    {
      "start_date": "2021-03-31T10:00:00Z",
      "end_date": "2021-03-31T11:00:00Z"
    },
    {
      "start_date": "2021-03-31T11:00:00Z",
      "end_date": "2021-03-31T12:00:00Z"
    },
    {
      "start_date": "2021-03-31T12:00:00Z",
      "end_date": "2021-03-31T13:00:00Z"
    },
    {
      "start_date": "2021-03-31T13:00:00Z",
      "end_date": "2021-03-31T14:00:00Z"
    },
    {
      "start_date": "2021-03-31T14:00:00Z",
      "end_date": "2021-03-31T15:00:00Z"
    },
    {
      "start_date": "2021-03-31T15:00:00Z",
      "end_date": "2021-03-31T16:00:00Z"
    },
    {
      "start_date": "2021-03-31T16:00:00Z",
      "end_date": "2021-03-31T17:00:00Z"
    },
    {
      "start_date": "2021-03-31T17:00:00Z",
      "end_date": "2021-03-31T18:00:00Z"
    },
    {
      "start_date": "2021-03-31T18:00:00Z",
      "end_date": "2021-03-31T19:00:00Z"
    },
    {
      "start_date": "2021-03-31T19:00:00Z",
      "end_date": "2021-03-31T20:00:00Z"
    },
    {
      "start_date": "2021-04-01T12:00:00Z",
      "end_date": "2021-04-01T13:00:00Z"
    },
    {
      "start_date": "2021-04-01T13:00:00Z",
      "end_date": "2021-04-01T14:00:00Z"
    },
    {
      "start_date": "2021-04-01T14:00:00Z",
      "end_date": "2021-04-01T15:00:00Z"
    },
    {
      "start_date": "2021-04-01T15:00:00Z",
      "end_date": "2021-04-01T16:00:00Z"
    },
    {
      "start_date": "2021-04-01T16:00:00Z",
      "end_date": "2021-04-01T17:00:00Z"
    },
    {
      "start_date": "2021-04-01T17:00:00Z",
      "end_date": "2021-04-01T18:00:00Z"
    },
    {
      "start_date": "2021-04-01T18:00:00Z",
      "end_date": "2021-04-01T19:00:00Z"
    },
    {
      "start_date": "2021-04-01T19:00:00Z",
      "end_date": "2021-04-01T20:00:00Z"
    },
    {
      "start_date": "2021-04-02T12:00:00Z",
      "end_date": "2021-04-02T13:00:00Z"
    },
    {
      "start_date": "2021-04-02T13:00:00Z",
      "end_date": "2021-04-02T14:00:00Z"
    },
    {
      "start_date": "2021-04-02T14:00:00Z",
      "end_date": "2021-04-02T15:00:00Z"
    },
    {
      "start_date": "2021-04-02T15:00:00Z",
      "end_date": "2021-04-02T16:00:00Z"
    },
    {
      "start_date": "2021-04-02T16:00:00Z",
      "end_date": "2021-04-02T17:00:00Z"
    },
    {
      "start_date": "2021-04-02T17:00:00Z",
      "end_date": "2021-04-02T18:00:00Z"
    },
    {
      "start_date": "2021-04-02T18:00:00Z",
      "end_date": "2021-04-02T19:00:00Z"
    },
    {
      "start_date": "2021-04-02T19:00:00Z",
      "end_date": "2021-04-02T20:00:00Z"
    },
    {
      "start_date": "2021-04-03T09:00:00Z",
      "end_date": "2021-04-03T10:00:00Z"
    },
    {
      "start_date": "2021-04-03T10:00:00Z",
      "end_date": "2021-04-03T11:00:00Z"
    },
    {
      "start_date": "2021-04-03T11:00:00Z",
      "end_date": "2021-04-03T12:00:00Z"
    },
    {
      "start_date": "2021-04-03T12:00:00Z",
      "end_date": "2021-04-03T13:00:00Z"
    },
    {
      "start_date": "2021-04-03T13:00:00Z",
      "end_date": "2021-04-03T14:00:00Z"
    },
    {
      "start_date": "2021-04-03T14:00:00Z",
      "end_date": "2021-04-03T15:00:00Z"
    },
    {
      "start_date": "2021-04-03T15:00:00Z",
      "end_date": "2021-04-03T16:00:00Z"
    },
    {
      "start_date": "2021-04-03T16:00:00Z",
      "end_date": "2021-04-03T17:00:00Z"
    },
    {
      "start_date": "2021-04-03T17:00:00Z",
      "end_date": "2021-04-03T18:00:00Z"
    },
    {
      "start_date": "2021-04-03T18:00:00Z",
      "end_date": "2021-04-03T19:00:00Z"
    },
    {
      "start_date": "2021-04-03T19:00:00Z",
      "end_date": "2021-04-03T20:00:00Z"
    },
    {
      "start_date": "2021-04-04T09:00:00Z",
      "end_date": "2021-04-04T10:00:00Z"
    },
    {
      "start_date": "2021-04-04T10:00:00Z",
      "end_date": "2021-04-04T11:00:00Z"
    },
    {
      "start_date": "2021-04-04T11:00:00Z",
      "end_date": "2021-04-04T12:00:00Z"
    },
    {
      "start_date": "2021-04-04T12:00:00Z",
      "end_date": "2021-04-04T13:00:00Z"
    },
    {
      "start_date": "2021-04-04T13:00:00Z",
      "end_date": "2021-04-04T14:00:00Z"
    },
    {
      "start_date": "2021-04-04T14:00:00Z",
      "end_date": "2021-04-04T15:00:00Z"
    },
    {
      "start_date": "2021-04-04T16:00:00Z",
      "end_date": "2021-04-04T17:00:00Z"
    },
    {
      "start_date": "2021-04-04T17:00:00Z",
      "end_date": "2021-04-04T18:00:00Z"
    },
    {
      "start_date": "2021-04-04T18:00:00Z",
      "end_date": "2021-04-04T19:00:00Z"
    },
    {
      "start_date": "2021-04-04T19:00:00Z",
      "end_date": "2021-04-04T20:00:00Z"
    },
    {
      "start_date": "2021-04-05T09:00:00Z",
      "end_date": "2021-04-05T10:00:00Z"
    },
    {
      "start_date": "2021-04-05T10:00:00Z",
      "end_date": "2021-04-05T11:00:00Z"
    },
    {
      "start_date": "2021-04-05T11:00:00Z",
      "end_date": "2021-04-05T12:00:00Z"
    },
    {
      "start_date": "2021-04-05T12:00:00Z",
      "end_date": "2021-04-05T13:00:00Z"
    },
    {
      "start_date": "2021-04-05T13:00:00Z",
      "end_date": "2021-04-05T14:00:00Z"
    },
    {
      "start_date": "2021-04-05T14:00:00Z",
      "end_date": "2021-04-05T15:00:00Z"
    },
    {
      "start_date": "2021-04-05T15:00:00Z",
      "end_date": "2021-04-05T16:00:00Z"
    },
    {
      "start_date": "2021-04-05T16:00:00Z",
      "end_date": "2021-04-05T17:00:00Z"
    },
    {
      "start_date": "2021-04-05T17:00:00Z",
      "end_date": "2021-04-05T18:00:00Z"
    },
    {
      "start_date": "2021-04-05T18:00:00Z",
      "end_date": "2021-04-05T19:00:00Z"
    },
    {
      "start_date": "2021-04-05T19:00:00Z",
      "end_date": "2021-04-05T20:00:00Z"
    },
    {
      "start_date": "2021-04-06T09:00:00Z",
      "end_date": "2021-04-06T10:00:00Z"
    },
    {
      "start_date": "2021-04-06T10:00:00Z",
      "end_date": "2021-04-06T11:00:00Z"
    },
    {
      "start_date": "2021-04-06T11:00:00Z",
      "end_date": "2021-04-06T12:00:00Z"
    },
    {
      "start_date": "2021-04-06T12:00:00Z",
      "end_date": "2021-04-06T13:00:00Z"
    },
    {
      "start_date": "2021-04-06T13:00:00Z",
      "end_date": "2021-04-06T14:00:00Z"
    },
    {
      "start_date": "2021-04-06T14:00:00Z",
      "end_date": "2021-04-06T15:00:00Z"
    },
    {
      "start_date": "2021-04-06T15:00:00Z",
      "end_date": "2021-04-06T16:00:00Z"
    },
    {
      "start_date": "2021-04-06T16:00:00Z",
      "end_date": "2021-04-06T17:00:00Z"
    },
    {
      "start_date": "2021-04-06T17:00:00Z",
      "end_date": "2021-04-06T18:00:00Z"
    },
    {
      "start_date": "2021-04-06T18:00:00Z",
      "end_date": "2021-04-06T19:00:00Z"
    },
    {
      "start_date": "2021-04-06T19:00:00Z",
      "end_date": "2021-04-06T20:00:00Z"
    }
  ]
}
```