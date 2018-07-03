import React, { Component, Fragment } from "react";
import PropTypes from "prop-types";
import { connect } from "react-redux";
import * as appActions from "../../app/actions";
import * as kingdomActions from "../../kingdom/actions";

import avatar from "../avatar_temp.png";
import styles from "./header-kingdom-section.css";
import { getKingdom, hasKingdom, isDeletingKingdom } from "../../kingdom/selectors";
import { Menu, MenuItem, Popover } from "@material-ui/core";

class HeaderKingdomSection extends Component {
  constructor(props) {
    super(props);
    this.state = {
      kingdomMenuAnchor: null,
      kingdomMenuOpen: false,
    };
  }

  onContainerClicked = event => {
    event.preventDefault();
    this.setState({
      kingdomMenuOpen: true,
      kingdomMenuAnchor: event.currentTarget,
    });
  };

  render() {
    const { hasKingdom, kingdom, deleteKingdom, deletingKingdom } = this.props;

    if (!hasKingdom) {
      return null;
    }

    const { kingdomMenuOpen, kingdomMenuAnchor } = this.state;

    return (
      <Fragment>
        <div
          className={styles.name__container}
          onClick={this.onContainerClicked}
        >
          <img src={avatar} className={styles.avatar} alt={"Kingdom avatar"}/>
          <div className={styles.name}>{kingdom.name}</div>
          <i className={"material-icons"}>arrow_drop_down</i>
        </div>
        <Popover
          anchorOrigin={{ horizontal: "center", vertical: "bottom" }}
          transformOrigin={{ vertical: "top", horizontal: "center" }}
          open={kingdomMenuOpen}
          onClose={() => this.setState({ kingdomMenuOpen: false })}
          anchorEl={kingdomMenuAnchor}
        >
          <Menu>
            <MenuItem
              value="Delete kingdom"
              primaryText={"Delete kingdom"}
              style={deletingKingdom ? { opacity: 0.5 } : {}}
              onClick={() => {
                this.setState({ kingdomMenuOpen: false });
                deleteKingdom();
              }}
            />
          </Menu>
        </Popover>
      </Fragment>
    )
  }
}

HeaderKingdomSection.propTypes = {
  hasKingdom: PropTypes.bool.isRequired,
  kingdom: PropTypes.object,
  deletingKingdom: PropTypes.bool.isRequired,
};

HeaderKingdomSection.defaultProps = {
  kingdom: null,
};

const mapStateToProps = state => {
  return {
    hasKingdom: hasKingdom(state),
    kingdom: getKingdom(state),
    deletingKingdom: isDeletingKingdom(state),
  };
};

export default connect(mapStateToProps, { ...appActions, ...kingdomActions })(
  HeaderKingdomSection,
);
