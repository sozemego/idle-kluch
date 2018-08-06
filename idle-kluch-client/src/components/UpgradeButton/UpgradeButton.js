import React from "react";
import PropTypes from "prop-types";
import Button from "@material-ui/core/Button/Button";
import styles from "./upgrade-button.css";

export const UpgradeButton = (props) => {
	return (
		<Button variant="extendedFab" aria-label="Delete" onClick={props.onClick} size={"small"}>
			<img src={"idle_buck_1.png"} className={styles.idlebucks}/>
			{`(${props.cost}) `}{props.text}
		</Button>
	)
};

UpgradeButton.propTypes = {
	text: PropTypes.string,
	onClick: PropTypes.func,
	cost: PropTypes.number,
};

UpgradeButton.defaultProps = {
	text: "Upgrade",
	onClick: () => {},
	cost: 0,
}

