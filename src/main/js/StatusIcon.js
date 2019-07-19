import React from "react";

type Props = {
  color: string,
  icon : string
};

class StatusIcon extends React.Component<Props> {
  render() {
    const { color, icon } = this.props;
    return  <i className={`fas fa-2x has-text-${color} fa-${icon}`}/>;
  }
}

export const SuccessIcon = () => <StatusIcon color="success" icon="check-circle" />;
export const FailureIcon = () => <StatusIcon color="danger" icon="times-circle" />;
export const UnstableIcon = () => <StatusIcon color="warning" icon="exclamation-circle" />;

export const PlaceholderIcon = () => <StatusIcon color="light" icon="circle-notch" />;
export const AbortedIcon = PlaceholderIcon;
export const PendingIcon = PlaceholderIcon;

