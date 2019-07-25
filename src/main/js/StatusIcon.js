import React  from "react";


type BaseProps = {
  title?: string,
  titleColor?: string
};

type Props = BaseProps & {
  color: string,
  icon : string,
  size: string
};

class StatusIcon extends React.Component<Props> {
  render() {
    const { color, icon, size, title } = this.props;
    return (
      <div>
        <i className={`fas fa-${size}x has-text-${color} fa-${icon}`}/>
        {title && <span style={{paddingLeft:"0.5rem"}}>{title}</span>}
      </div>
    );
  }
}

export const SuccessIcon: React.SFC<BaseProps> = (props) => <StatusIcon color="success" icon="check-circle" size="1" {...props} />;
export const FailureIcon: React.SFC<BaseProps> = (props) => <StatusIcon color="danger" icon="times-circle" size="1" {...props}/>;
export const UnstableIcon: React.SFC<BaseProps> = (props) => <StatusIcon color="warning" icon="exclamation-circle" size="1" {...props} />;

export const PlaceholderIcon: React.SFC<BaseProps> = (props) => <StatusIcon color="light" icon="circle-notch" size="1" {...props} />;

export default StatusIcon;

