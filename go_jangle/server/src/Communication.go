package main

import (
	"fmt"
	"container/list"
)


func Listen_To_Clients(user *User, e *list.Element){
	//Array to store data read from client
	read_data := make([]byte, 1024);

	for {
		//Read data from client
		len, err := (*user).Read(read_data);
		//If server fails to read from client,
		//the user has disconnected and can be
		//removed from the lsit fo connections
		if err != nil {
			jangle.userlist.Remove(e);
			fmt.Println("User Disconnected");
			break;
		}
		//Send read array to Message file for parsing and processing
		Parse_Data(user, read_data[:len]);
	}
}

func Send_Message(user *User, message Message) uint{
	user.Write(message.Build_Message())
	
	return 0;
}

func Send_Broadcast(message Message){
	for e := jangle.userlist.Front(); e != nil; e = e.Next() {
		e.Value.(*User).Write(message.Build_Message())
	}			
}

func (u *User) Read(read_data []byte) (int, error){
	return (*(*u).c).Read(read_data)
}

func (u *User) Write(write_data []byte) (int, error){
	return (*(*u).c).Write(write_data)
}

func (u *User) Printf(format string, a ...interface{}) (int, error){
	return fmt.Fprintf((*(*u).c), format, a...)
}

func (u *User) Scanf(format string, a ...interface{}) (int, error){
	return fmt.Fscanf(*(*u).c, format, a...)
}
