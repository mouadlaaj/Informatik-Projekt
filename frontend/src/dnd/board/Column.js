import React from "react";
import styledx from "@xstyled/styled-components";
import { grid, borderRadius } from "../styles/constants";
import { Draggable } from "react-beautiful-dnd";
import QuoteList from "../styles/list";
import Title from "../styles/title";

import { styled} from "@mui/material/styles";
import {SpeedDial, Tooltip } from "@mui/material";
import TaskAdd from "../../Components/Task/taskAdd";
import { Dialog} from "@mui/material";
import { MdOutlinePostAdd } from "react-icons/md";
const Container = styledx.div`
  margin: ${grid}px;
  display: flex;
  flex-direction: column;
  background-color: aliceblue;
  border : 1px SOLID lightgrey;
  box-shadow:10px;
  border-radius: 10px;
  min-height: 450px;
  max-height: 450px;
  
  &:hover {
    // transition: transform .5s;
    // transform: scale(1.05);
    //background-color: #DFE1E6;
    
    //color: #262673;
  }
`;

const Header = styledx.div`
  display: flex;
  align-items: center;
  justify-content: center;
  border-top-left-radius: ${borderRadius}px;
  border-top-right-radius: ${borderRadius}px;
  
  transition: background-color 0.2s ease;
`;

const Column = (props) => {
  const title = props.title;
  const quotes = props.quotes;
  const count = props.count;
  const index = props.index;
  const getAllTasksForUser = props.getAllTasksForUser;
  const [isAddTaskOpen, setIsAddTaskOpen] = React.useState(false);

  const toggleAddTaskModal = () => {
    localStorage.setItem("isPopUpOpen", "true");
    setIsAddTaskOpen(!isAddTaskOpen);
    if (isAddTaskOpen === true) {
      getAllTasksForUser();
      localStorage.setItem("isPopUpOpen", "false"); 
    }
  };

  const isManager = localStorage.getItem("role") === "MANAGER";

  const BootstrapDialog = styled(Dialog)(({ theme }) => ({
    "& .MuiDialog-paper": {
      maxWidth: "800px",
      height: "auto",
    },
    "& .MuiDialogActions-root": {
      padding: theme.spacing(1),
    },
  }));

  return (
    <>
      <Draggable draggableId={title} index={index}>
        {(provided, snapshot) => (
          <Container ref={provided.innerRef} {...provided.draggableProps}>
            <Header isDragging={snapshot.isDragging}>
              <Title
                isDragging={snapshot.isDragging}
                {...provided.dragHandleProps}
                aria-label={`${title} quote list`}
              >
                {title}
                {" (" + count + ")"}
              </Title>
              {title === "TODO" && isManager && (
                <Tooltip title="Add new task" arrow>
                  <SpeedDial
                    ariaLabel="Add Task"
                    sx={{ position: "fixed", bottom: 20, right: 20 }}
                    icon={<MdOutlinePostAdd size={"25px"} />}
                    onClick={toggleAddTaskModal}
                    className="hover:-translate-y-1 hover:scale-110 duration-300"
                  />
                </Tooltip>
              )}
            </Header>
            <QuoteList
              listId={title}
              listType="QUOTE"
              style={{
                height: "420px",
              }}
              getAllTasksForUser={getAllTasksForUser}
              quotes={quotes}
              title={title}
              count={count}
              internalScroll={props.isScrollable}
              isCombineEnabled={Boolean(props.isCombineEnabled)}
            />
          </Container>
        )}
      </Draggable>

      <BootstrapDialog
        onClose={toggleAddTaskModal}
        aria-labelledby="customized-dialog-title"
        open={isAddTaskOpen}
      >

        <TaskAdd addTasksModal={toggleAddTaskModal} />
      </BootstrapDialog>
    </>
  );
};

export default Column;
