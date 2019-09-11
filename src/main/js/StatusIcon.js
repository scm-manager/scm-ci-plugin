//@flow
import React from "react";

type BaseProps = {
  titleType?: string,
  title?: string
};
type Props = BaseProps & {
  color: string,
  icon: string,
  size: string
};

class StatusIcon extends React.Component<Props> {
  static defaultProps = {
    color: "grey-lighter",
    icon: "circle-notch",
    size: "1x"
  };

  render() {
    const { color, icon, size, titleType, title } = this.props;
    return (
      <div>
        <i className={`fas fa-${size} has-text-${color} fa-${icon}`} />
        {(titleType || title) && (
          <span style={{ paddingLeft: "0.5rem" }}>
            {titleType && <strong>{titleType}: </strong>}
            {title && title}
          </span>
        )}
      </div>
    );
  }
}

export const getColor = ciStatus => {
  if (ciStatus && ciStatus.filter(ci => ci.status === "FAILURE").length > 0) {
    return "danger";
  } else if (
    ciStatus &&
    ciStatus.filter(ci => ci.status === "UNSTABLE").length > 0
  ) {
    return "warning";
  } else if (ciStatus && ciStatus.every(ci => ci.status === "SUCCESS")) {
    return "success";
  } else {
    return "secondary";
  }
};

export const getIcon = ciStatus => {
  if (ciStatus && ciStatus.filter(ci => ci.status === "FAILURE").length > 0) {
    return "times-circle";
  } else if (
    ciStatus &&
    ciStatus.filter(ci => ci.status === "UNSTABLE").length > 0
  ) {
    return "exclamation-circle";
  } else if (ciStatus && ciStatus.every(ci => ci.status === "SUCCESS")) {
    return "check-circle";
  } else {
    return "circle-notch";
  }
};

export const SuccessIcon: React.SFC<BaseProps> = props => (
  <StatusIcon color="success" icon="check-circle" {...props} />
);
export const FailureIcon: React.SFC<BaseProps> = props => (
  <StatusIcon color="danger" icon="times-circle" {...props} />
);
export const UnstableIcon: React.SFC<BaseProps> = props => (
  <StatusIcon color="warning" icon="exclamation-circle" {...props} />
);

export default StatusIcon;
