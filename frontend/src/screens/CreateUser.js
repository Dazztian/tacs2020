import React, {useState} from 'react'
import {StyleSheet, Text, View, Modal} from "react-native-web";
import {TextInput, Button} from 'react-native-paper'
import { Container } from '@material-ui/core';


const CreateUser = ()=>{
    const[name, setName] = useState("")
    const[password, setPassword] = useState("")
    const[email, setEmail] = useState("")
    const[modal, setModal] = useState(false)

    return(
        <Container style={{flex:1,
        marginTop:'15%',
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
            <TextInput
                label='Email'
                style={styles.inputStyle}
                value={email}
                theme={theme}
                mode="outlined"
                onChangeText={text =>setEmail(text) }
            />
            <Button  style={styles.botones} mode="contained" theme={theme}
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

export default CreateUser