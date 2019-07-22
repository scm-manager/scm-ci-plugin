import React from "react";

type Props = {
  color: string,
  icon : string,
  size: string,
  title?: string
};

class StatusIcon extends React.Component<Props> {
  render() {
    const { color, icon, size, title } = this.props;
    return (
      <div>
        <i className={`fas fa-${size}x has-text-${color} fa-${icon}`}/>
        {title && <span style={{paddingLeft:"0.25rem"}}>{title}</span>}
      </div>
    );
  }
}

export const SuccessIcon = () => <StatusIcon color="success" icon="check-circle" size="1.8"/>;
export const FailureIcon = () => <StatusIcon color="danger" icon="times-circle" size="1.8"/>;
export const UnstableIcon = () => <StatusIcon color="warning" icon="exclamation-circle" size="1.8" />;

export const PlaceholderIcon = () => <StatusIcon color="light" icon="circle-notch" size="1.8" />;
export const AbortedIcon = PlaceholderIcon;
export const PendingIcon = PlaceholderIcon;

export default StatusIcon;

