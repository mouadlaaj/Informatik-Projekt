import axios from 'axios';
import { toast } from 'react-toastify';
const BACKEND_APP_URL = "http://localhost:8003/api/v1/";
export const WEBSOCKET_URL = "http://localhost:8003/ws";

    export function logout(){
              toast("Session expired!");
              localStorage.removeItem("firstname");
              localStorage.removeItem("lastname");
              localStorage.removeItem("email");
              localStorage.removeItem("userId");
              localStorage.removeItem("token");
              localStorage.removeItem("role");
              localStorage.removeItem("isTL");
              window.location.replace("/")
    }

export function stringAvatar(name) {
  return {
    children: `${name.split(' ')[0][0].toUpperCase()}${name.split(' ')[1][0].toUpperCase()}`,
  };
}

export function timeAgo(date) {
  const now = new Date();
  const inputDate = new Date(date);
  const diffInSeconds = Math.floor((now - inputDate) / 1000);

  const intervals = [
    { label: 'yr', seconds: 31536000 },
    { label: 'mon', seconds: 2592000 },
    { label: 'day', seconds: 86400 },
    { label: 'hr', seconds: 3600 },
    { label: 'min', seconds: 60 },
    { label: 'sec', seconds: 1 }
  ];
  console.log(diffInSeconds);
  for (const interval of intervals) {
    const count = Math.floor(diffInSeconds / interval.seconds);
    if (count > 0) {
      return count === 1 ? `1 ${interval.label} ago` : `${count} ${interval.label}s ago`;
    }
  }

  return 'just now';
}


export const loginUser = (username, password) => {
  return axios.post(BACKEND_APP_URL + 'auth/login', {
    userName: username,
    password: password
  }, {
    headers: {
      'Accept': '*/*',
      'Content-Type': 'application/json',
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Headers": "*"
    }
  })
}

export const registerUser = (formData) => {
  return axios.post(BACKEND_APP_URL + 'auth/register', formData, {
    headers: {
      'Content-Type': 'application/json',
    }
  });
};


export const logoutUser = () => {
  return axios.post(BACKEND_APP_URL + 'auth/logout', {}, {
    headers: {'Content-Type': 'application/json',"Authorization": "Bearer " + localStorage.getItem("token")},
  })
}


export const markAsReadNotify = (id, status) => {
  return axios({
    url: BACKEND_APP_URL+"notification/"+id+"/"+status,
    method: "PUT",
    headers: {'Content-Type': 'application/json',"Authorization": "Bearer " + localStorage.getItem("token")},
    data: JSON.stringify({
      })
  });
}



export const getMemberById = (id) => {

  return fetch(BACKEND_APP_URL + "members/" + id, {
    method: "GET",
    headers: {
      'Content-Type': 'application/json',
      "Authorization": "Bearer " + localStorage.getItem("token")
    },
  })
    .catch(error => {
      console.log("Error while getAllTeams");
    })
}

export const getNotifications = () => {

  return fetch(BACKEND_APP_URL + "notification" , {
    method: "GET",
    headers: {
      'Content-Type': 'application/json',
      "Authorization": "Bearer " + localStorage.getItem("token")
    },
  })
    .catch(error => {
      console.log("Error while getAllTeams");
    })
}

export const getAllAdmins = () => {

  return fetch(BACKEND_APP_URL + "members/admins", {
    method: "GET",
    headers: {
      'Content-Type': 'application/json',
      "Authorization": "Bearer " + localStorage.getItem("token")
    },
  })
    .catch(error => {
      console.log("Error while getAllTeams");
    })
}

export const getAllActiveMembers = () => {

  return fetch(BACKEND_APP_URL + "members", {
    method: "GET",
    headers: {
      'Content-Type': 'application/json',
      "Authorization": "Bearer " + localStorage.getItem("token")
    },
  })
    .catch(error => {
      console.log("Error while getAllTeams");
    })
}

export const getAllTeamMembers = () => {

  return fetch(BACKEND_APP_URL + "members/team-members/"+localStorage.getItem("userId"), {
    method: "GET",
    headers: {
      'Content-Type': 'application/json',
      "Authorization": "Bearer " + localStorage.getItem("token")
    },
  })
    .catch(error => {
      console.log("Error while getAllTeams");
    })
}

export const getAllMembers = (text) => {

  return fetch(BACKEND_APP_URL + "members/search?searchTerm=" + text, {
    method: "GET",
    headers: {
      'Content-Type': 'application/json',
      "Authorization": "Bearer " + localStorage.getItem("token")
    },
  })
    .catch(error => {
      console.log("Error while getAllTeams");
    })
}

export const addNewMember = (memberId, password, fname, lname, mobile, email, designation, role, address, experience, doj, dob, gender) => {

  return axios.post(BACKEND_APP_URL + 'members', {
    firstName: fname,
    lastName: lname,
    phoneNumber: mobile,
    password: password,
    emailId: email,
    designation: designation,
    address: address,
    yearsOfExperience: experience,
    dateOfJoining: doj,
    dob: dob,
    gender: gender,
    memberId: memberId,
    role: role,
    createdBy: localStorage.getItem("userId"),
  }, {
    headers: {
      'Accept': '*/*',
      "Authorization": "Bearer " + localStorage.getItem("token"),
      'Content-Type': 'application/json',
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Headers": "*"
    }
  })
}

export const editNewMember = (memberId, password, fname, lname, mobile, email, designation, role, address, experience, doj, dob, gender) => {

  return axios.put(BACKEND_APP_URL + 'members/' + memberId, {
    firstName: fname,
    lastName: lname,
    phoneNumber: mobile,
    password: password,
    emailId: email,
    designation: designation,
    address: address,
    yearsOfExperience: experience,
    dateOfJoining: doj,
    dob: dob,
    gender: gender,
    memberId: memberId,
    role: role,
    modifiedBy: localStorage.getItem("userId"),
  }, {
    headers: {
      'Accept': '*/*',
      "Authorization": "Bearer " + localStorage.getItem("token"),
      'Content-Type': 'application/json',
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Headers": "*"
    }
  })
}

export const deleteMemberById = (memberId) => {

  return axios.delete(BACKEND_APP_URL + 'admin/' + memberId,
    {
      headers: {
        "Authorization": "Bearer " + localStorage.getItem("token"),
      },
      data: {

      }
    });
}



export const getTeamById = (id) => {

  return fetch(BACKEND_APP_URL + "teams/" + id, {
    method: "GET",
    headers: {
      'Content-Type': 'application/json',
      "Authorization": "Bearer " + localStorage.getItem("token")
    },
  })
    .catch(error => {
      console.log("Error while getAllTeams");
    })
}


export const getAllTeams = (text) => {

  return fetch(BACKEND_APP_URL + "teams?search=" + text, {
    method: "GET",
    headers: {
      'Content-Type': 'application/json',
      "Authorization": "Bearer " + localStorage.getItem("token")
    },
  })
    .catch(error => {
      console.log("Error while getAllTeams");
    })
}

export const addNewTeam = (teamName, teamLeadId, teamMembers) => {

  return axios.post(BACKEND_APP_URL + 'teams', {
    teamLeadId: teamLeadId,
    teamName: teamName,
    teamMembers: teamMembers,
    createdBy: localStorage.getItem("userId"),
  }, {
    headers: {
      'Accept': '*/*',
      "Authorization": "Bearer " + localStorage.getItem("token"),
      'Content-Type': 'application/json',
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Headers": "*"
    }
  })
}

export const editNewTeam = (teamId, teamName, teamLeadId, teamMembers) => {

  return axios.put(BACKEND_APP_URL + 'teams/' + teamId, {
    teamLeadId: teamLeadId,
    teamName: teamName,
    teamMembers: teamMembers,
    modifiedBy: localStorage.getItem("userId"),
  }, {
    headers: {
      'Accept': '*/*',
      "Authorization": "Bearer " + localStorage.getItem("token"),
      'Content-Type': 'application/json',
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Headers": "*"
    }
  })
}

export const deleteTeamById = (teamId) => {

  return axios.delete(BACKEND_APP_URL + 'teams/' + teamId,
    {
      headers: {
        "Authorization": "Bearer " + localStorage.getItem("token"),
      },
      data: {

      }
    });
}



export const getAllTasks = (title, project, tag, memberName, startDate, endDate, completedDate, assignedDate, priority) => {
 

  let url = "tasks/search/first?";
  
  
  if(title){
    url = url+"title="+title+"&";
  }
  if(project){
    url = url+"projectName="+project+"&";
  }
  if(tag){
    url = url+"tag="+tag+"&";
  }
  if(memberName){
    url = url+"searchMemberId="+memberName+"&";
  }
  if(startDate){
    url = url+"startDate="+startDate+"&";
  }
  if(endDate){
    url = url+"endDate="+endDate+"&";
  }
  if(completedDate){
    url = url+"completedDate="+completedDate+"&";
  }
  if(assignedDate){
    url = url+"assignedDate="+assignedDate+"&";
  }
  if(priority){
    url = url+"taskPriority="+priority+"&";
  }
  
  return fetch(BACKEND_APP_URL + url, {
    method: "GET",
    headers: {
      'Content-Type': 'application/json',
      "Authorization": "Bearer " + localStorage.getItem("token")
    },
  })
    .catch(error => {
      console.log("Error while getAllTeams");
    })
}

export const getTasksByStatus = (status, pageNo, pageSize, title, project, tag, memberName, startDate, endDate, completedDate, assignedDate, priority) => {
  
  let url = "tasks/search/page?";
  
  if(status){
    url = url+"status="+status+"&";
  }

  if(pageNo){
    url = url+"pageNumber="+pageNo+"&";
  }

  if(pageSize){
    url = url+"pageSize="+pageSize+"&";
  }
  
  if(title){
    url = url+"title="+title+"&";
  }
  if(project){
    url = url+"projectName="+project+"&";
  }
  if(tag){
    url = url+"tag="+tag+"&";
  }
  if(memberName){
    url = url+"memberName="+memberName+"&";
  }
  if(startDate){
    url = url+"startDate="+startDate+"&";
  }
  if(endDate){
    url = url+"endDate="+endDate+"&";
  }
  if(completedDate){
    url = url+"completedDate="+completedDate+"&";
  }
  if(assignedDate){
    url = url+"assignedDate="+assignedDate+"&";
  }
  if(priority){
    url = url+"taskPriority="+priority+"&";
  }
  
  return fetch(BACKEND_APP_URL + url, {
    method: "GET",
    headers: {
      'Content-Type': 'application/json',
      "Authorization": "Bearer " + localStorage.getItem("token")
    },
  })
    .catch(error => {
      console.log("Error while getAllTeams");
    })
}


export const changeTaskStatus = (taskId, status, message, type) => {

  return axios.put(BACKEND_APP_URL + 'tasks/status/' + taskId, {
    status: status,
    type: type,
    message: message,
    memberId: localStorage.getItem("userId"),
  }, {
    headers: {
      'Accept': '*/*',
      "Authorization": "Bearer " + localStorage.getItem("token"),
      'Content-Type': 'application/json',
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Headers": "*"
    }
  })
}


export const getProjectById = (projectId) => {
  return fetch(BACKEND_APP_URL + "project/" + projectId, {
    method: "GET",
    headers: {
      'Content-Type': 'application/json',
      "Authorization": "Bearer " + localStorage.getItem("token")
    },
  })
    .catch(error => {
      console.log("Error while getAllTeams");
    })
}

export const getAllProjectsUnderAdmin = () => {
  return fetch(BACKEND_APP_URL + "project/admin", {
    method: "GET",
    headers: {
      'Content-Type': 'application/json',
      "Authorization": "Bearer " + localStorage.getItem("token")
    },
  })
    .catch(error => {
      console.log("Error while getAllTeams");
    })
}

export const getAllProject = (text) => {

  return fetch(BACKEND_APP_URL + "project?search=" + text, {
    method: "GET",
    headers: {
      'Content-Type': 'application/json',
      "Authorization": "Bearer " + localStorage.getItem("token")
    },
  })
    .catch(error => {
      console.log("Error while getAllTeams");
    })
}

export const getAllTagByProjectId = (projectId) => {

  return fetch(BACKEND_APP_URL + "project/tag/" + projectId, {
    method: "GET",
    headers: {
      'Content-Type': 'application/json',
      "Authorization": "Bearer " + localStorage.getItem("token")
    },
  })
    .catch(error => {
      console.log("Error while getAllTeams");
    })
}


export const getAllMembersByProjectIdAndTeams = (projectId) => {

  return axios.get(BACKEND_APP_URL + 'project/members/'+projectId, {
    headers: {
      'Accept': '*/*',
      "Authorization": "Bearer " + localStorage.getItem("token"),
      'Content-Type': 'application/json',
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Headers": "*"
    }
  })
}

export const getAllDesignersByProjectId = (projectId) => {
  return axios.get(BACKEND_APP_URL +'project/designers/'+projectId, {
    headers: {
      'Accept': '*/*',
      'Authorization': 'Bearer ' + localStorage.getItem('token'),
      'Content-Type': 'application/json',
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Headers': '*',
      'USERNAME': localStorage.getItem('username') // required by your backend
    }
  });
};

export const getAllDevelopersByProjectId = (projectId) => {
  return axios.get(BACKEND_APP_URL +'project/developers/' +projectId, {
    headers: {
      'Accept': '*/*',
      'Authorization': 'Bearer ' + localStorage.getItem('token'),
      'Content-Type': 'application/json',
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Headers': '*',
      'USERNAME': localStorage.getItem('username') // required
    }
  });
};

export const getAllTestersByProjectId = (projectId) => {
  return axios.get(BACKEND_APP_URL +'project/testers/' +projectId, {
    headers: {
      'Accept': '*/*',
      'Authorization': 'Bearer ' + localStorage.getItem('token'),
      'Content-Type': 'application/json',
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Headers': '*',
      'USERNAME': localStorage.getItem('username') // required
    }
  });
};

export const updateProject = (projectId, projectName, description, teams, tags) => {

  return axios.put(BACKEND_APP_URL + 'project/' + projectId, {
    projectName: projectName,
    description: description,
    teams: teams,
    tags: tags,
    modifiedBy: localStorage.getItem("userId"),
  }, {
    headers: {
      'Accept': '*/*',
      "Authorization": "Bearer " + localStorage.getItem("token"),
      'Content-Type': 'application/json',
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Headers": "*"
    }
  })
}
export const addNewProject = (projectName, description, teams, tags) => {

  return axios.post(BACKEND_APP_URL + 'project', {
    projectName: projectName,
    description: description,
    teams: teams,
    tags: tags,
    createdBy: localStorage.getItem("userId"),
  }, {
    headers: {
      'Accept': '*/*',
      "Authorization": "Bearer " + localStorage.getItem("token"),
      'Content-Type': 'application/json',
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Headers": "*"
    }
  })
}

export const addAttachmnts = (formData) => {
  return axios({
    url: BACKEND_APP_URL+"tasks/attachment",
    method: "POST",
    headers: {'Content-Type': 'multipart/form-data',"Authorization": "Bearer " + localStorage.getItem("token")},
    data: formData
  });
}


export const deleteProjectById = (projectId) => {

  return axios.delete(BACKEND_APP_URL + 'project/' + projectId,
    {
      headers: {
        "Authorization": "Bearer " + localStorage.getItem("token"),
      },
      data: {

      }
    });
}


export const createTask = (title, shortDescription, description, priority, startDate, endDate, projectId, tags, assignedTo, complexity) => {

  console.log("=======SDATE", startDate);
  console.log("=======EDATE", endDate);

  return axios.post(BACKEND_APP_URL + 'tasks', {
    title: title,
    description: description,
    shortDescription: shortDescription,
    projectId: projectId,
    startDate: startDate,
    endDate: endDate,
    priority: priority,
    assignedTo: assignedTo,
    assignedBy: assignedTo.length > 0 ? localStorage.getItem("userId") : null,
    projectId: projectId,
    tags: tags,
    complexity: complexity,
    createdBy: localStorage.getItem("userId"),
  }, {
    headers: {
      'Accept': '*/*',
      "Authorization": "Bearer " + localStorage.getItem("token"),
      'Content-Type': 'application/json',
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Headers": "*"
    }
  })
}   


export const deleteTask = (taskId) => {

  return axios.delete(BACKEND_APP_URL + 'tasks/'+taskId,
    {
      headers: {
        "Authorization": "Bearer " + localStorage.getItem("token"),
      },
      data: {

      }
    });
}


export const patchUpdateTask = (taskId, data) => {

  return axios.put(BACKEND_APP_URL + 'tasks/'+taskId, data
  // {
  //   title: title,
  //   description: description,
  //   shortDescription: shortDescription,
  //   projectId: projectId,
  //   startDate: startDate,
  //   endDate: endDate,
  //   priority: priority,
  //   assignedTo: assignedTo,
  //   assignedBy: assignedTo.length > 0 ? localStorage.getItem("userId") : null,
  //   projectId: projectId,
  //   assignedTeams: teams,
  //   modifiedBy: localStorage.getItem("userId"),
  // }
  
  , {
    headers: {
      'Accept': '*/*',
      "Authorization": "Bearer " + localStorage.getItem("token"),
      'Content-Type': 'application/json',
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Headers": "*"
    }
  })
}


export const getParticularTaskById = (taskId) => {
  return fetch(BACKEND_APP_URL + "tasks/" + taskId, {
    method: "GET",
    headers: {
      'Content-Type': 'application/json',
      "Authorization": "Bearer " + localStorage.getItem("token")
    },
  })
    .catch(error => {
      console.log("Error while getAllTeams");
    })
}

export const addComments = (taskId, message) => {

  return axios.post(BACKEND_APP_URL + 'tasks/comment',
  {
    taskId: taskId,
    message: message,
    createdBy: localStorage.getItem("userId"),
  }
  
  , {
    headers: {
      'Accept': '*/*',
      "Authorization": "Bearer " + localStorage.getItem("token"),
      'Content-Type': 'application/json',
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Headers": "*"
    }
  })
}

export const deleteAttachmentById = (attachId) => {

  return axios.delete(BACKEND_APP_URL + 'tasks/attachment/'+attachId,
    {
      headers: {
        "Authorization": "Bearer " + localStorage.getItem("token"),
      },
      data: {

      }
    });
}

export const deleteCommentById = (commentId, taskId) => {

  return axios.delete(BACKEND_APP_URL + 'tasks/comment/'+commentId+"/"+taskId,
    {
      headers: {
        "Authorization": "Bearer " + localStorage.getItem("token"),
      },
      data: {

      }
    });
}



export const getIndividualPerformance = (teamId, memberId, startDate, endDate) => {
  const params = new URLSearchParams();
 
  if (teamId) params.append("teamId", teamId);
  if (memberId) params.append("memberId", memberId);
  if (startDate) params.append("startDate", startDate);
  if (endDate) params.append("endDate", endDate);
 
  const queryString = params.toString() ? `?${params.toString()}` : "";
  const url = `${BACKEND_APP_URL}report/task-report${queryString}`;
 
  console.log("Fetching report from:", url);
 
  return fetch(url, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      Authorization: "Bearer " + localStorage.getItem("token"),
    },
  }).catch((error) => {
    console.error("Error while fetching individual performance:", error);
  });
};
 
export const getOverallTaskCount = () => {
  return fetch(BACKEND_APP_URL + "report/overall-task", {
    method: "GET",
    headers: {
      'Content-Type': 'application/json',
      "Authorization": "Bearer " + localStorage.getItem("token")
    },
  })
    .catch(error => {
      console.log("Error while getAllTeams");
    })
}

export const reportBugs = (formData) => {
  console.log("Payload sent to backend:", formData);

  return axios.post(`${BACKEND_APP_URL}bugs`, formData, {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + localStorage.getItem("token"),
    }
  });
};

export const updateBugStatus = (taskId, bugUpdates) => {
  return axios.put(
    `${BACKEND_APP_URL}bugs/${taskId}`,
    bugUpdates,
    {
      headers: {
        Accept: "*/*",
        Authorization: "Bearer " + localStorage.getItem("token"),
        "Content-Type": "application/json",
      },
    }
  );
};
 

export const deleteBugById = (taskId, bugId) => {
  return axios.delete(`${BACKEND_APP_URL}bugs/${taskId}/${bugId}`, {
    headers: {
      "Authorization": "Bearer " + localStorage.getItem("token"),
    }
  });
};


export const assignQcMember = (taskId, startDate, endDate) => {
  return axios.put(BACKEND_APP_URL + 'tasks/work-status', {
    taskId: taskId,
    startTime: startDate,
    endTime: endDate,
  }, {
    headers: {
      'Accept': '*/*',
      "Authorization": "Bearer " + localStorage.getItem("token"),
      'Content-Type': 'application/json',
    }
  });
};


export const getDesignationWiseReportStatus = async (designation) => {
  try {
    const response = await axios.get(BACKEND_APP_URL + 'report/designation-wise-status', {
      params: {
        designation: designation
      },
      headers: {
        'Accept': '*/*',
        'Authorization': 'Bearer ' + localStorage.getItem('token'),
        'Content-Type': 'application/json',
      }
    });
    return response.data;
  } catch (error) {
    return null;
  }
};