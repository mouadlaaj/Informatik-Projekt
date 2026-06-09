import React, { useEffect, useRef, useState } from 'react';
import styled from '@xstyled/styled-components';
import { Droppable, Draggable } from 'react-beautiful-dnd';
import QuoteItem from './item';
import { grid } from './constants';
import debounce from 'lodash.debounce';
import Title from './title';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import {Tooltip } from '@mui/joy';

const Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  padding: ${grid}px;
  border: ${grid}px;
  padding-bottom: 0;
  transition: background-color 0.2s ease, opacity 0.1s ease;
  user-select: none;
  width: 280px;
`;

const scrollContainerHeight = 380;

const DropZone = styled.div`
  min-height: ${scrollContainerHeight}px;
  padding-bottom: ${grid}px;
`;

const ScrollContainer = styled.div`
  overflow-x: hidden;
  overflow-y: auto;
  -ms-overflow-style: none;
  max-height: ${scrollContainerHeight}px;
  scrollbar-width: none;
  &::-webkit-scrollbar {
    display: none; /* Hide scrollbar */
  }
`;
const StyledKeyboardArrowUpIcon = styled(KeyboardArrowUpIcon)`
  color: #262673;
  &:hover {
    color: orange;
  }
`;

const StyledKeyboardArrowDownIcon = styled(KeyboardArrowDownIcon)`
  color: #262673;
  &:hover {
    color: orange;
  }
`;


const Container = styled.div``;

const InnerQuoteList = React.memo(function InnerQuoteList(props) {
  return (
    props &&
    props.quotes &&
    props.quotes.length > 0 &&
    Array.isArray(props.quotes) &&
    props.quotes.map((quote, index) => (
      <Draggable key={quote.id} draggableId={quote.id} index={index}>
        {(dragProvided, dragSnapshot) => (
          <QuoteItem
            key={quote.id}
            quote={quote}
            isDragging={dragSnapshot.isDragging}
            getAllTasksForUser={props.getAllTasksForUser}
            isGroupedOver={Boolean(dragSnapshot.combineTargetFor)}
            provided={dragProvided}
            style={{ borderRadius: '20px' }}
          />
        )}
      </Draggable>
    ))
  );
});

function InnerList(props) {
  const { quotes, dropProvided, title } = props;
  const titleComponent = title ? <Title>{title}</Title> : null;

  return (
    <Container>
      <DropZone ref={dropProvided.innerRef}>
        <InnerQuoteList
          quotes={quotes}
          getAllTasksForUser={props.getAllTasksForUser}
        />
        {dropProvided.placeholder}
      </DropZone>
    </Container>
  );
}

export default function QuoteList(props) {
  const {
    ignoreContainerClipping,
    internalScroll,
    scrollContainerStyle,
    isDropDisabled,
    isCombineEnabled,
    listId = 'LIST',
    listType,
    count,
    getAllTasksForUser,
    style,
    quotes,
    title,
    useClone,
  } = props;

  const [page, setPage] = useState(0);
  const itemsPerPage = 10;

  useEffect(() => {
    setPage(0);
  }, [count]);

  const scrollContainerRef = useRef(null);

  const startIndex = page >= 0 ? page * itemsPerPage + 1 : 1;
  const endIndex = startIndex + quotes.length - 1;
  let newPage = page;
  const clickPage = debounce(async (direction) => {
    
    if (direction === "next" && (page + 1) * itemsPerPage < count) {
      newPage = page + 1;
    } else if (direction === "prev" && page > 0) {
      newPage = page - 1;
    }

    if (newPage !== page) {
      await getAllTasksForUser(newPage, title);
      setPage(newPage);

      if (scrollContainerRef.current) {
        scrollContainerRef.current.scrollTo({
          top: 0,
          behavior: 'smooth',
        });
      }
    }
  }, 200);

  return (
    <Droppable
      droppableId={listId}
      type={listType}
      ignoreContainerClipping={ignoreContainerClipping}
      isDropDisabled={isDropDisabled}
      isCombineEnabled={isCombineEnabled}
      renderClone={
        useClone
          ? (provided, snapshot, descriptor) => (
              <QuoteItem
                quote={quotes[descriptor.source.index]}
                getAllTasksForUser={getAllTasksForUser}
                provided={provided}
                isDragging={snapshot.isDragging}
                isClone
              />
            )
          : null
      }
    >
      {(dropProvided, dropSnapshot) => (
        <Wrapper
          style={style}
          isDraggingOver={dropSnapshot.isDraggingOver}
          isDropDisabled={isDropDisabled}
          isDraggingFrom={Boolean(dropSnapshot.draggingFromThisWith)}
          {...dropProvided.droppableProps}
        >

          {
            page !==0 ?
            <>
            
            <Tooltip color='primary' variant='soft'  title={`Page number : ${page + 1}, Records [${startIndex} to ${endIndex}]`}  arrow size='sm'>
          <div
            onClick={() => clickPage("prev")}
            style={{ marginTop: '-15px', cursor: 'pointer', textAlign:'center', padding:'1px' }}
          >
              <StyledKeyboardArrowUpIcon />
             </div> 
             </Tooltip>
            </>:""
          }
          
         
          {internalScroll ? (
            <ScrollContainer
              style={scrollContainerStyle}
              ref={scrollContainerRef}
            >
              <InnerList
                getAllTasksForUser={getAllTasksForUser}
                quotes={quotes}
                title={title}
                dropProvided={dropProvided}
              />
            </ScrollContainer>
          ) : (
            <InnerList
              getAllTasksForUser={getAllTasksForUser}
              quotes={quotes}
              title={title}
              dropProvided={dropProvided}
            />
          )}

          {
            (page + 1) * itemsPerPage < count ? 
            <>
            <Tooltip color='primary' variant='soft' title={`Page number : ${page + 1}, Records [${startIndex} to ${endIndex}]`} arrow size='sm'>
            <div
            onClick={() => clickPage("next")}
            style={{ textAlign: 'center', padding: '5px', cursor: 'pointer' }}
          >
           <StyledKeyboardArrowDownIcon />
          </div></Tooltip>
            </>:""
          }
          
        </Wrapper>
      )}
    </Droppable>
  );
}
