import React, { Component } from 'react';
import { Provider } from "react-redux";
import getMuiTheme from 'material-ui/styles/getMuiTheme';
import lightBaseTheme from 'material-ui/styles/baseThemes/lightBaseTheme';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';

import store from "../store/store";
import Header from '../header/Header.js';

import styles from './App.css';

const theme = getMuiTheme(lightBaseTheme);

class App extends Component {

  render() {
	return (
	  <MuiThemeProvider muiTheme={theme}>
		<Provider store={store}>
		  <div>
			<Header/>
			<div className={styles.content}>
			  <div>here be one of menus</div>
			  <div>here be game</div>
			</div>
		  </div>
		</Provider>
	  </MuiThemeProvider>
	);
  }
}

export default App;
