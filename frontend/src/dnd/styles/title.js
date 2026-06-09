import styled from '@xstyled/styled-components';
import { grid } from './constants';

// $ExpectError - not sure why
export default styled.h6`
  padding: ${grid}px;
  color:#262672;
  font-size: 15px;
  font-weight: 600;
  text-align: center;
  transition: background-color ease 0.2s;
  flex-grow: 1;
  user-select: none;
  position: relative;
  &:focus {
    outline: 2px solid #998dd9;
    outline-offset: 2px;
  }
`;
