import React, {useState} from 'react'
import {StyleSheet, Text, View, Modal} from "react-native-web";
import {TextInput, Button} from 'react-native-paper'
import { Container } from '@material-ui/core';
import {  Link} from "react-router-dom";


const CreateUser = ()=>{
    const[name, setName] = useState("")
    const[password, setPassword] = useState("")
    const[email, setEmail] = useState("")
    const[modal, setModal] = useState(false)

    
    const submitData = ()=>{
        fetch("http://localhost:8080/createUser",{
            method:"POST",
            headers:{
              'Content-Type': 'application/json'
            },
            body:JSON.stringify({
                name,
                email,
                password
            })
        })
        .then(res=>res.json())
        .then(data=>{
            window.alert(`${data.name} is saved successfuly`)        
        })
        .then(
            <Link to="/" style={{color: 'blue'}} activeStyle={{color: 'red'}}>LoginPapu</Link>)
        .catch(err=>{
            window.alert(err)
            //window.alert("something went wrong")
      })
    }
    

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
            <Button style={styles.botones} mode="contained" theme={theme}
                    onPress={()=>submitData()}>
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