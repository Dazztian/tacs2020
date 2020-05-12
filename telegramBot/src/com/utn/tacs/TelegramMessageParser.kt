package com.utn.tacs

class TelegramMessageParser{
    fun parse(countryData: CountryData): String {
        return "countryregion: "+countryData.countryregion +"\n"+
                "lastUpdate: "+countryData.lastupdate +"\n"+
                "location: "+countryData.lastupdate +"\n"+
                "countryCode: "+parse(countryData.countrycode) +"\n"+
                "confirmed: \n"+countryData.confirmed +"\n"+
                "deaths: "+countryData.deaths +"\n"+
                "recovered: "+countryData.recovered
    }
    fun parse(countryCode: CountryCode): String{
        return "iso2: "+countryCode.iso2+"\n"+
                "iso3: "+countryCode.iso3
    }
}