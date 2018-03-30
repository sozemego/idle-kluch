import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import * as appActions from '../../app/actions';
import * as kingdomActions from '../../kingdom/actions';

import avatar from '../avatar_temp.png';
import styles from './header-kingdom-section.css';
import { getKingdom, hasKingdom } from '../../kingdom/selectors';
import { Menu, MenuItem, Popover } from 'material-ui';

class HeaderKingdomSection extends Component {

  constructor(props) {
	super(props);
	this.state = {
	  kingdomMenuAnchor: null,
	  kingdomMenuOpen: false,
	};
  }

  onContainerClicked = (event) => {
	event.preventDefault();
	this.setState({ kingdomMenuOpen: true, kingdomMenuAnchor: event.currentTarget });
  };

  render() {
	const {
	  hasKingdom,
	  kingdom,
	  deleteKingdom,
	} = this.props;

	if (!hasKingdom) {
	  return null;
	}

	const {
	  kingdomMenuOpen,
	  kingdomMenuAnchor,
	} = this.state;

	return [
	  <div className={styles.name__container}
		   key={0}
		   onClick={this.onContainerClicked}>
		<img src={avatar} className={styles.avatar} alt={'Kingdom avatar'}/>
		<div className={styles.name}>
		  {kingdom.name}
		</div>
		<i className={'material-icons'}>arrow_drop_down</i>
	  </div>,
	  <Popover anchorOrigin={{ horizontal: 'middle', vertical: 'bottom' }}
			   open={kingdomMenuOpen}
			   key={1}
			   onRequestClose={() => this.setState({ kingdomMenuOpen: false })}
			   anchorEl={kingdomMenuAnchor}
	  >
		<Menu>
		  <MenuItem value="Delete kingdom"
					primaryText={'Delete kingdom'}
					onClick={() => {
					  this.setState({ kingdomMenuOpen: false });
					  deleteKingdom();
					}}
		  />
		</Menu>
	  </Popover>
	];
  }

}

HeaderKingdomSection.propTypes = {
  hasKingdom: PropTypes.bool.isRequired,
  kingdom: PropTypes.object,
};

HeaderKingdomSection.defaultProps = {
  kingdom: null,
};

const mapStateToProps = (state) => {
  return {
	hasKingdom: hasKingdom(state),
	kingdom: getKingdom(state),
  };
};

export default connect(mapStateToProps, { ...appActions, ...kingdomActions })(HeaderKingdomSection);

