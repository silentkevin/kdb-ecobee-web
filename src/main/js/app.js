'use strict';

// tag::vars[]
const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');
const Iframe = require("react-iframe");
const Button = require('react-bootstrap').Button;
const ee = require('event-emitter');
// end::vars[]

let emitter = ee({}), listener;

// tag::app[]
class App extends React.Component {
    constructor(props) {
        super(props);
        this.state = {users: [], user: {}, ecobeeUser: {}};
    }

    refreshUser() {
        client({method: 'GET', path: '/user'}).done(response => {
            response.entity.user.then(response => {
                this.setState({user: response.entity});
            });
            response.entity.ecobeeUser.then(response => {
                this.setState({ecobeeUser: response.entity});
            });
        });
    }

    componentDidMount() {
        let upperThis = this;
        upperThis.refreshUser();

        emitter.on('refreshUser', listener = function (args) {
            upperThis.refreshUser();
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
        if (!user.name) {
            return ( <div>loading user</div> );
        } else if (ecobeeUser && !ecobeeUser.accessToken && ecobeeUser.pinCode) {
            return (
                <AuthorizeView user={user} ecobeeUser={ecobeeUser}/>
            );
        } else if (ecobeeUser && user && ecobeeUser.accessToken) {
            return (
                <ThermostatListView user={user} ecobeeUser={ecobeeUser}/>
            );
        } else {
            return ( <div>unknown state</div> );
        }
    }
}
// end::user-auth-stateful-view[]

// tag::thermostat-list-view[]
class ThermostatListView extends React.Component {
    constructor(props) {
        super(props);
        this.state = {thermostats: []};
    }

    componentDidMount() {
        let upperThis = this;
        upperThis.refreshThermostats();

        emitter.on('refreshThermostats', listener = function (args) {
            upperThis.refreshThermostats();
        });
    }

    refreshThermostats() {
        let ecobeeUser = this.props.ecobeeUser;
        ecobeeUser.thermostats.then(response => {
            this.setState({ thermostats: response.entity._embedded.thermostats });
        });
    }

    onClickRefresh() {
        emitter.emit("refreshThermostats");
    }

    render() {
        let user = this.props.user;
        let ecobeeUser = this.props.ecobeeUser;
        let thermostats = this.state.thermostats.map(thermostat =>
            <Thermostat key={thermostat._links.self.href} thermostat={thermostat}/>
        );
        return (
            <div>
                {thermostats}
                <div>
                    <Button bsStyle="success" bsSize="large" onClick={this.onClickRefresh}>
                        Refresh
                    </Button>
                </div>
            </div>
        );
    }
}
// end::thermostat-list-view[]

// tag::thermostat[]
class Thermostat extends React.Component {
    render() {
        var t = this.props.thermostat;
        return (
            <div>
                <div><span>Name:</span> <span>{t.name}</span></div>
                <div><span>Current Temperature:</span> <span>{t.currentTemperature}</span></div>
                <div><span>HVAC Mode:</span> <span>{t.hvacMode}</span></div>
            </div>
        );
    }
}
// end::thermostat[]

// tag::authorize-view[]
class AuthorizeView extends React.Component {
    onClickAuthorize() {
        client({method: 'POST', path: '/user/authorize'}).done(response => {
            emitter.emit('refreshUser');
        });
    }

    render() {
        let user = this.props.user;
        let ecobeeUser = this.props.ecobeeUser;
        return (
            <div>
                <div>Please log in to www.ecobee.com below and enter the pin <b>{ecobeeUser.pinCode}</b> in the "Settings" tab and then "My Apps"</div>
                <div>
                    <Button bsStyle="success" bsSize="large" onClick={this.onClickAuthorize}>
                        I Did It You Bastard
                    </Button>
                </div>
                <EcobeeLoginFrame/>
            </div>
        );
    }
}
// end::authorize-view[]

class EcobeeLoginFrame extends React.Component {
    render() {
        return (
            <Iframe url="https://www.ecobee.com/home/secure/settings.jsf" width="100%" height="600px"/>
        );
    }
}

// tag::render[]
ReactDOM.render(
    <App />,
    document.getElementById('react')
);
// end::render[]
