import React from 'react';
import {StyleSheet, Text,View} from "react-native-web";
import { Card} from 'react-native-paper'

function Home(){
    return(
        <Card>
        <Text>ESTA ES UNA PAGINA DE PRUEBA</Text>
    </Card>

)
}

const styles= StyleSheet.create({
    container:{
        flex:1,
        backgroundColor:'#fff',
        alignItems:'center',
        justifyContent:'center'
    }

})

export default Home