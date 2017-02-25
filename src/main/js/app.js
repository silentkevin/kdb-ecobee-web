'use strict';

// tag::vars[]
const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');
const Iframe = require("react-iframe");
const Button = require('react-bootstrap').Button;
const ButtonGroup = require('react-bootstrap').ButtonGroup;
const ee = require('event-emitter');
const moment = require('moment');
// end::vars[]

let emitter = ee({}), listener;

// tag::app[]
class App extends React.Component {
    constructor(props) {
        super(props);
        this.state = {user: {}, ecobeeUser: {}};
    }

    refreshUser() {
        this.state = {user: {}, ecobeeUser: {}};
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
        emitter.emit("refreshUser");
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
                    <Button bsStyle="success" onClick={this.onClickRefresh}>
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
        let t = this.props.thermostat;
        let holdUntil = t.holdUntil ? moment(t.holdUntil).format("hh:mm a") : '';
        let degrees = [66, 68, 70, 72, 74, 76, 78, 80, 90];
        if (t.hvacMode === "heat") {
            degrees = [50, 58, 60, 62, 64, 66, 68, 70];
        }
        let tempButtons = degrees.map(deg => {
            return ( <Button key={deg} bsStyle={t.desiredTemperature === deg ? "info" : "default"}>{deg}</Button> );
        });
        let timeButtons = ["Schedule", "2H", "4H", "8H", "NT", "Hold"].map(holdMode => {
            return ( <Button key={holdMode} bsStyle={t.holdMode === holdMode ? "info" : "default"}>{holdMode}</Button> );
        });
        return (
            <div>
                <div><span>Name:</span> <span>{t.name}</span></div>
                <div><span>Current Temperature:</span> <span>{t.currentTemperature}°F</span></div>
                <div><span>Desired Temperature:</span> <span>{t.desiredTemperature}°F</span></div>
                <div><span>Hold Until:</span> <span>{holdUntil}</span></div>
                <div><span>HVAC Mode:</span> <span>{t.hvacMode}</span></div>
                <div>
                    <ButtonGroup>
                        {tempButtons}
                    </ButtonGroup>
                </div>
                <div>
                    <ButtonGroup>
                        {timeButtons}
                    </ButtonGroup>
                </div>
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
