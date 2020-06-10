export default {
  tasks: [
    {
      id: 0,
      type: "Meeting",
      title: "Meeting with Andrew Piker",
      time: "9:00"
    },
    {
      id: 1,
      type: "Call",
      title: "Call with HT Company",
      time: "12:00"
    },
    {
      id: 2,
      type: "Meeting",
      title: "Meeting with Zoe Alison",
      time: "14:00"
    },
    {
      id: 3,
      type: "Interview",
      title: "Interview with HR",
      time: "15:00"
    }
  ],
  bigStat: [
    {
      product: "Light Blue",
      total: {
        monthly: 4232,
        weekly: 1465,
        daily: 199,
        percent: { value: 3.7, profit: false }
      },
      color: "primary",
      registrations: {
        monthly: { value: 830, profit: false },
        weekly: { value: 215, profit: true },
        daily: { value: 33, profit: true }
      },
      bounce: {
        monthly: { value: 4.5, profit: false },
        weekly: { value: 3, profit: true },
        daily: { value: 3.25, profit: true }
      }
    },
    {
      product: "Sing App",
      total: {
        monthly: 754,
        weekly: 180,
        daily: 27,
        percent: { value: 2.5, profit: true }
      },
      color: "warning",
      registrations: {
        monthly: { value: 32, profit: true },
        weekly: { value: 8, profit: true },
        daily: { value: 2, profit: false }
      },
      bounce: {
        monthly: { value: 2.5, profit: true },
        weekly: { value: 4, profit: false },
        daily: { value: 4.5, profit: false }
      }
    },
    {
      product: "RNS",
      total: {
        monthly: 1025,
        weekly: 301,
        daily: 44,
        percent: { value: 3.1, profit: true }
      },
      color: "secondary",
      registrations: {
        monthly: { value: 230, profit: true },
        weekly: { value: 58, profit: false },
        daily: { value: 15, profit: false }
      },
      bounce: {
        monthly: { value: 21.5, profit: false },
        weekly: { value: 19.35, profit: false },
        daily: { value: 10.1, profit: true }
      }
    }
  ],
  notifications: [
    {
      id: 0,
      icon: "thumbs-up",
      color: "primary",
      content:
        'Ken <span className="fw-semi-bold">accepts</span> your invitation'
    },
    {
      id: 1,
      icon: "file",
      color: "success",
      content: "Report from LT Company"
    },
    {
      id: 2,
      icon: "envelope",
      color: "danger",
      content: '4 <span className="fw-semi-bold">Private</span> Mails'
    },
    {
      id: 3,
      icon: "comment",
      color: "success",
      content: '3 <span className="fw-semi-bold">Comments</span> to your Post'
    },
    {
      id: 4,
      icon: "cog",
      color: "light",
      content: 'New <span className="fw-semi-bold">Version</span> of RNS app'
    },
    {
      id: 5,
      icon: "bell",
      color: "info",
      content:
        '15 <span className="fw-semi-bold">Notifications</span> from Social Apps'
    }
  ],
  table: [
    {
      id: 0,
      country: "Argentina",
      infected: "1000000",
      recovered: "1000000",
      deceased: "1000000",
    },
    {
      id: 1,
      country: "Brasil",
      infected: "1000000",
      recovered: "1000000",
      deceased: "1000000",
    },
    {
      id: 2,
      country: "Chile",
      infected: "1000000",
      recovered: "1000000",
      deceased: "1000000",
    },
    {
      id: 3,
      country: "Peru",
      infected: "1000000",
      recovered: "1000000",
      deceased: "1000000",
    },
    {
      id: 4,
      country: "Mark Otto",
      infected: "1000000",
      recovered: "1000000",
      deceased: "1000000",
    },
    {
      id: 5,
      country: "Mark Otto",
      infected: "1000000",
      recovered: "1000000",
      deceased: "1000000",
    },
    {
      id: 6,
      country: "Mark Otto",
      infected: "1000000",
      recovered: "1000000",
      deceased: "1000000",
    },
    {
      id: 7,
      country: "Mark Otto",
      infected: "1000000",
      recovered: "1000000",
      deceased: "1000000",
    },
    {
      id: 8,
      country: "Mark Otto",
      infected: "1000000",
      recovered: "1000000",
      deceased: "1000000",
    },
    {
      id: 9,
      country: "Mark Otto",
      infected: "1000000",
      recovered: "1000000",
      deceased: "1000000",
    },
    {
      id: 10,
      country: "Jamaica",
      infected: "1000000",
      recovered: "1000000",
      deceased: "1000000",
    },
    {
      id: 11,
      country: "Jamaica",
      infected: "1000000",
      recovered: "1000000",
      deceased: "1000000",
    },
    {
      id: 12,
      country: "Jamaica",
      infected: "1000000",
      recovered: "1000000",
      deceased: "1000000",
    },
    {
      id: 13,
      country: "Jamaica",
      infected: "1000000",
      recovered: "1000000",
      deceased: "1000000",
    },
    {
      id: 14,
      country: "Jamaica",
      infected: "1000000",
      recovered: "1000000",
      deceased: "1000000",
    }
  ],
  near: [
    {
      "_id": "5ede4f65853cd27b821cecec",
      "countryregion": "Argentina",
      "lastupdate": "2020-06-08T14:42:00.008Z",
      "location": {
        "lat": -38.4161,
        "lng": -63.6167
      },
      "countrycode": {
        "iso2": "AR",
        "iso3": "ARG"
      },
      "confirmed": 22794,
      "deaths": 664,
      "recovered": 6909,
      "timeseries": []
    },
    {
      "_id": "5ede4f65853cd27b821cecf9",
      "countryregion": "Bolivia",
      "lastupdate": "2020-06-08T14:42:00.008Z",
      "location": {
        "lat": -16.2902,
        "lng": -63.5887
      },
      "countrycode": {
        "iso2": "BO",
        "iso3": "BOL"
      },
      "confirmed": 13643,
      "deaths": 465,
      "recovered": 2086,
      "timeseries": []
    },
    {
      "_id": "5ede4f65853cd27b821cecfb",
      "countryregion": "Brazil",
      "lastupdate": "2020-06-08T14:42:00.008Z",
      "location": {
        "lat": -14.235,
        "lng": -51.9253
      },
      "countrycode": {
        "iso2": "BR",
        "iso3": "BRA"
      },
      "confirmed": 691758,
      "deaths": 36455,
      "recovered": 283952,
      "timeseries": []
    },
    {
      "_id": "5ede4f65853cd27b821ced05",
      "countryregion": "Chile",
      "lastupdate": "2020-06-08T14:42:00.008Z",
      "location": {
        "lat": -35.6751,
        "lng": -71.543
      },
      "countrycode": {
        "iso2": "CL",
        "iso3": "CHL"
      },
      "confirmed": 134150,
      "deaths": 1637,
      "recovered": 108150,
      "timeseries": []
    },
    {
      "_id": "5ede4f65853cd27b821ced5a",
      "countryregion": "Paraguay",
      "lastupdate": "2020-06-08T14:42:00.008Z",
      "location": {
        "lat": -23.4425,
        "lng": -58.4438
      },
      "countrycode": {
        "iso2": "PY",
        "iso3": "PRY"
      },
      "confirmed": 1135,
      "deaths": 11,
      "recovered": 575,
      "timeseries": []
    },
    {
      "_id": "5ede4f65853cd27b821ced5b",
      "countryregion": "Peru",
      "lastupdate": "2020-06-08T14:42:00.008Z",
      "location": {
        "lat": -9.19,
        "lng": -75.0152
      },
      "countrycode": {
        "iso2": "PE",
        "iso3": "PER"
      },
      "confirmed": 196515,
      "deaths": 5465,
      "recovered": 86219,
      "timeseries": []
    },
    {
      "_id": "5ede4f65853cd27b821ced80",
      "countryregion": "Uruguay",
      "lastupdate": "2020-06-08T14:42:00.008Z",
      "location": {
        "lat": -32.5228,
        "lng": -55.7658
      },
      "countrycode": {
        "iso2": "UY",
        "iso3": "URY"
      },
      "confirmed": 845,
      "deaths": 23,
      "recovered": 730,
      "timeseries": []
    }
  ]
  
};
