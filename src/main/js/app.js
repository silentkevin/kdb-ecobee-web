'use strict';

// tag::vars[]
const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');
const Iframe = require("react-iframe");
// end::vars[]

// tag::app[]
class App extends React.Component {
    constructor(props) {
        super(props);
        this.state = {users: [], user: {}, ecobeeUser: {}};
    }

    componentDidMount() {
        client({method: 'GET', path: '/user'}).done(response => {
            response.entity.user.then(response => {
                this.setState({user: response.entity});
            });
            response.entity.ecobeeUser.then(response => {
                this.setState({ecobeeUser: response.entity});
            });
        });
    }

    render() {
        return (
            <UserAuthStatefulView user={this.state.user} ecobeeUser={this.state.ecobeeUser}/>
        )
    }
}
// end::app[]

// tag::user-auth-stateful-view[]
class UserAuthStatefulView extends React.Component {
    render() {
        let user = this.props.user;
        let ecobeeUser = this.props.ecobeeUser;
        if (!ecobeeUser.accessToken) {
            return (
                <AuthorizeView user={user} ecobeeUser={ecobeeUser}/>
            );
        } else {
            return ( <div>to be determined</div> );
        }
    }
}
// end::user-auth-stateful-view[]

const onButtonPress = () => {
    Alert.alert('Button has been pressed!');
};

// tag::user-auth-stateful-view[]
const onClickAuthorize = function() {
    debugger;
};

class AuthorizeView extends React.Component {
    render() {
        let user = this.props.user;
        let ecobeeUser = this.props.ecobeeUser;
        return (
            <div>
                <div>Please log in to www.ecobee.com below and enter the pin <b>{ecobeeUser.pinCode}</b> in the "Settings" tab and then "My Apps"</div>
                <button onClick={onClickAuthorize}>
                    I Did It You Bastard
                </button>
                <EcobeeLoginFrame/>
            </div>
        );
    }
}
// end::user-auth-stateful-view[]

class EcobeeLoginFrame extends React.Component {
    render() {
        return (
            <Iframe url="https://www.ecobee.com/home/secure/settings.jsf" width="1024px" height="600px"/>
        );
    }
}

// tag::render[]
ReactDOM.render(
    <App />,
    document.getElementById('react')
);
// end::render[]
