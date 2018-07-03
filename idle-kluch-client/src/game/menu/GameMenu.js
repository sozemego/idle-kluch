import React, { Component, Fragment } from "react";
import PropTypes from "prop-types";
import { connect } from "react-redux";
import * as gameActions from "../actions";
import { Dialog } from "@material-ui/core";
import { isAlreadyConnected } from "../../app/selectors";
import BuildingList from "./buildinglist/BuildingList";

class GameMenu extends Component {

  getAlreadyConnectedDialog = () => {
    const {
      alreadyConnected,
    } = this.props;

    if(!alreadyConnected) {
      return null;
    }

    return (
      <Dialog open={true}>
        You are already connected to the game in another tab or browser. Please close this tab.
        This message will later be expanded to account for users with your username logged in the same browser (another tab)
        or another browser altogether.
      </Dialog>
    );
  };

  render() {
    const { getAlreadyConnectedDialog } = this;

    return (
      <Fragment>
        <BuildingList/>
        {getAlreadyConnectedDialog()}
      </Fragment>
    );
  }
}

GameMenu.propTypes = {
  alreadyConnected: PropTypes.bool.isRequired,
};

const mapStateToProps = state => {
  return {
    alreadyConnected: isAlreadyConnected(state),
  };
};

export default connect(mapStateToProps, gameActions)(GameMenu);
