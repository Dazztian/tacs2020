import React, {useState} from 'react'
import {StyleSheet, Text, View, Modal} from "react-native-web";
import {TextInput, Button} from 'react-native-paper'
import { Container } from '@material-ui/core';
import { BrowserRouter as Router, Switch, Route, Link} from "react-router-dom";


const Login = ()=>{
    const[name, setName] = useState("")
    const[password, setPassword] = useState("")
    const[modal, setModal] = useState(false)

    return(
        <Container style={{flex:1,
            marginTop:'30%',
            width:'40%',
            flexDirection:'row',
            alignItems:'center',
            justifyContent:'center'}}>
            <View style={styles.root}>
                <TextInput
                    label='Name'
                    style={styles.inputStyle}
                    value={name}
                    theme={theme}
                    mode="outlined"
                    onChangeText={text =>setName(text) }
                />
                <TextInput
                    label='Password'
                    style={styles.inputStyle}
                    value={password}
                    theme={theme}
                    mode="outlined"
                    onChangeText={text =>setPassword(text) }
                />
                <Button style={styles.botones} mode="contained" theme={theme}
                        onPress={()=>setModal(true)}>
                    LOGIN
                </Button>
                <Button style={styles.botones} mode="contained" theme={theme}
                        onPress={()=>setModal(true)}>
                    CREAR USUARIO
                </Button>

            </View>
        </Container>


    )
}

const theme = {
    colors:{
        primary:"#006aff"

    }
}

const styles = StyleSheet.create({
    root:{
        flex:1
    },
    botones:{
        marginTop:5
    },
    inputStyle:{
        margin:5
    }

})

export default Login