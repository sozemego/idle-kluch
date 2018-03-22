import React, {Component} from 'react';
import PropTypes from 'prop-types';
import { connect } from "react-redux";
import * as gameActions from './actions';

class GameContainer extends Component {

  constructor(props) {
    super(props);
  }

  componentDidMount = () => {
    this.props.connect();
  };

  render() {
    return(
      <div>
		THIS A GAME
	  </div>
	)
  }

}

const mapStateToProps = (state) => {
  return {

  };
};

export default connect(mapStateToProps, gameActions)(GameContainer);