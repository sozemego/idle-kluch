import React, { Component, Fragment } from "react";
import PropTypes from "prop-types";
import { connect } from "react-redux";
import * as appActions from "../../app/actions";
import { getUser, isLoggedIn } from "../../app/selectors";

import avatar from "../avatar_temp.png";
import styles from "./header-user.css";
import Menu from "@material-ui/core/es/Menu/Menu";
import MenuItem from "@material-ui/core/es/MenuItem/MenuItem";

class HeaderUserComponent extends Component {
  constructor(props) {
    super(props);
    this.state = {
      userMenuAnchor: null,
    };
  }

  onContainerClicked = event => {
    event.preventDefault();
    this.setState({ userMenuOpen: true, userMenuAnchor: event.currentTarget });
  };

  render() {
    const { userMenuAnchor } = this.state;

    const { user, isLoggedIn, logout } = this.props;

    if (!isLoggedIn) {
      return null;
    }

    return (
      <Fragment>
        <div
          className={styles.name__container}
          onClick={this.onContainerClicked}
        >
          <img src={avatar} className={styles.avatar} alt={"User avatar"}/>
          <div className={styles.name}>{user.name}</div>
          <i className={"material-icons"}>arrow_drop_down</i>
        </div>
        <Menu
          open={Boolean(userMenuAnchor)}
          onClose={() => this.setState({ userMenuAnchor: null })}
          anchorEl={userMenuAnchor}
        >
          <MenuItem onClick={() => {
            this.setState({ userMenuAnchor: null });
            logout();
          }}>
            Logout
          </MenuItem>
        </Menu>
      </Fragment>
    )
  }
}

HeaderUserComponent.propTypes = {
  user: PropTypes.object.isRequired,
  isLoggedIn: PropTypes.bool.isRequired,
};

const mapStateToProps = state => {
  return {
    user: getUser(state),
    isLoggedIn: isLoggedIn(state),
  };
};

export default connect(mapStateToProps, appActions)(HeaderUserComponent);
