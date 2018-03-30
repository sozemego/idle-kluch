import React, { Component } from 'react';
import { Provider } from 'react-redux';
import getMuiTheme from 'material-ui/styles/getMuiTheme';
import lightBaseTheme from 'material-ui/styles/baseThemes/lightBaseTheme';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';

import { createBrowserHistory } from 'history';
import { Router } from 'react-router-dom';

import store from '../store/store';
import Header from '../header/Header.js';

import styles from './App.css';
import ContentContainer from './ContentContainer';

import * as appActions from './actions';

const theme = getMuiTheme(lightBaseTheme);
const history = createBrowserHistory();

class App extends Component {

	componentDidMount = () => {
		store.dispatch(appActions.init());
	};

	render() {
		return (
				<Provider store={store}>
					<MuiThemeProvider muiTheme={theme}>
						<Router history={history}>
							<div>
								<Header/>
								<div className={styles.content}>
									{/*<div>here be one of menus</div>*/}
									<ContentContainer/>
								</div>
							</div>
						</Router>
					</MuiThemeProvider>
				</Provider>
		);
	}
}

export default App;
