'use strict';

// tag::vars[]
const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');
const Iframe = require("react-iframe");
const Button = require('react-bootstrap').Button;
const ButtonGroup = require('react-bootstrap').ButtonGroup;
const ee = require('event-emitter');
const moment = require('moment-timezone');
const $ = require('jquery');
// end::vars[]

let emitter = ee({}), listener;

let csrfToken = $("meta[name='_csrf']").attr("content");
let csrfHeaderName = $("meta[name='_csrf_header']").attr("content");

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
        this.refreshThermostats();
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
        let thermostats = this.state.thermostats.map(thermostat =>
            <Thermostat key={thermostat._links.self.href} thermostat={thermostat}/>
        );
        return (
            <div>
                <div>Welcome, {this.props.user.name}, your time zone is {this.props.user.timeZone}</div>
                <div>
                    {thermostats}
                </div>
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
    constructor(props) {
        super(props);
    }

    setHold() {
        let desiredTemp = this.desiredTemperature;
        let holdMode = this.holdMode;
        let entity = {
            thermostatName: this.props.thermostat.name,
            desiredTemperature: desiredTemp,
            holdMode: holdMode
        };
        let headers = {
            'Content-Type': 'application/hal+json'
        };
        headers[csrfHeaderName] = csrfToken;
        client({
            method: 'POST',
            path: '/user/hold',
            entity: entity,
            headers: headers
        }).done(response => {
            emitter.emit('refreshUser');
        });
    }

    onClickDesiredTemperature(deg) {
        this.desiredTemperature = deg;
        this.setHold();
    }

    onClickHoldMode(holdMode) {
        this.holdMode = holdMode;
        this.setHold();
    }

    componentWillMount() {
        this.desiredTemperature = this.props.thermostat.desiredTemperature;
        this.holdMode = this.props.thermostat.holdAction;
    }

    render() {
        let t = this.props.thermostat;

        let holdUntilMoment = t.holdUntil ? moment(t.holdUntil).tz("America/New_York") : null;
        let holdUntil = holdUntilMoment ? holdUntilMoment.format("hh:mm a") : '';
        if (holdUntilMoment) {
            let diff = holdUntilMoment.diff(moment());
            let duration = moment.duration(diff);
            let f = duration.humanize();
            let millis = duration.asMilliseconds();
            if (millis > 86400000) {
                holdUntil = "forever"
            }
        }

        let degrees = [66, 68, 70, 72, 74, 76, 78, 80, 90];
        if (t.hvacMode === "heat") {
            degrees = [50, 58, 60, 62, 64, 66, 68, 70];
        }
        let tempButtons = degrees.map(deg => {
            return (
                <Button
                    key={deg}
                    bsStyle={t.desiredTemperature === deg ? "info" : "default"}
                    bsSize="large"
                    onClick={this.onClickDesiredTemperature.bind(this, deg)}
                >
                    {deg}°F
                </Button> );
        });
        let timeButtons = ["Resume Schedule", "2 Hours", "4 Hours", "8 Hours", "Next Transition", "Hold Forever"].map(holdMode => {
            return (
                <Button
                    key={holdMode}
                    bsStyle={t.holdMode === holdMode ? "info" : "default"}
                    bsSize="large"
                    onClick={this.onClickHoldMode.bind(this, holdMode)}
                >
                    {holdMode}
                </Button> );
        });
        return (
            <div>
                <div><span>Name:</span> <span>{t.name}</span></div>
                <div><span>Current Temperature:</span> <span>{t.currentTemperature}°F</span></div>
                <div><span>Desired Temperature:</span> <span>{t.desiredTemperature}°F</span></div>
                <div><span>Hold Until:</span> <span>{holdUntil}</span></div>
                <div><span>Hold Action:</span> <span>{t.holdAction}</span></div>
                <div><span>HVAC Mode:</span> <span>{t.hvacMode}</span></div>
                <div>
                    <span>
                        <ButtonGroup vertical>
                            {tempButtons}
                        </ButtonGroup>
                    </span>
                    <span>
                        <ButtonGroup vertical>
                            {timeButtons}
                        </ButtonGroup>
                    </span>
                </div>
            </div>
        );
    }
}
// end::thermostat[]

// tag::authorize-view[]
class AuthorizeView extends React.Component {
    onClickAuthorize() {
        let headers = {
            'Content-Type': 'application/hal+json'
        };
        headers[csrfHeaderName] = csrfToken;
        client({
            method: 'POST',
            path: '/user/authorize',
            headers: headers
        }).done(response => {
            emitter.emit('refreshUser');
        });
    }

    onClickRegenerate() {
        let headers = {
            'Content-Type': 'application/hal+json'
        };
        headers[csrfHeaderName] = csrfToken;
        client({
            method: 'POST',
            path: '/user/regenerate',
            headers: headers
        }).done(response => {
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
                        I Did It
                    </Button>
                    <Button bsStyle="success" bsSize="large" onClick={this.onClickRegenerate}>
                        Regenerate PIN Code
                    </Button>
                </div>
                {/*<EcobeeLoginFrame/>*/}
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
