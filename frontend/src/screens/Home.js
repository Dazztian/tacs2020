import React from 'react';
import {StyleSheet, Text,View} from "react-native-web";
import {Card, FAB} from 'react-native-paper'

const Home= (props)=>{
    return(
        <View>
        <Card>
        <Text style={styles.container}>Covid 19</Text>
        </Card>
            <FAB onPress={()=> props.navigation.navigate("Create")}
            small={false}
            icon="plus"
            theme={{colors:{accent:"#006aff"}}}
            onPress={()=>console.log('Pressed')}
            />
        </View>

)
}

const styles= StyleSheet.create({
    container:{
        flex:1,
        backgroundColor:'#fff',
        alignItems:'center',
        fontSize:'100%',
        justifyContent:'center',
        alignContent:'center',
        height:'20%'
    }

})

export default Home