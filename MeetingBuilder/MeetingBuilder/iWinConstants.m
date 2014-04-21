//
//  iWinConstants.m
//  MeetingBuilder
//
//  Created by CSSE Department on 2/2/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import "iWinConstants.h"

@implementation iWinConstants

const int MODAL_XOFFSET = 0;
const int MODAL_YOFFSET = 20;
const int MODAL_HEIGHT = 1040;
const int MODAL_WIDTH = 768;
const NSString *DATABASE_URL = @"http://csse371-04.csse.rose-hulman.edu";
NSString* const VIEW_AND_SCHEDULE_MEETING_NIB = @"iWinScheduleViewMeetingViewController";
NSString* const ADD_AND_VIEW_TASK_NIB = @"iWinAddAndViewTaskViewController";
NSString* const USER_ID_KEY = @"userID";
NSString* const CONTACT_ENTITY = @"Contact";
NSString* const SETTINGS_ENTITY = @"Settings";
NSString* const EMAIL_KEY = @"email";
NSString* const PASSWORD_KEY = @"password";
NSString* const OK_BUTTON = @"Ok";
NSString* const ERROR_MESSAGE = @"Error";
NSString* const REMEMBER_ME_ENTITY = @"RememberMe";
NSString* const REGISTER_VC_NIB_NAME = @"iWinRegisterViewController";
NSString* const LOGIN_VC_NIB_NAME = @"iWinLoginViewController";
NSString* const MENU_VC_NIB_NAME = @"iWinMenuViewController";
NSString* const SCHEDULE_VC_NIB_NAME = @"iWinScheduleViewController";
NSString* const HOME_SCREEN_VC_NIB_NAME = @"iWinHomeScreenViewController";
NSString* const MEETING_VC_NIB_NAME = @"iWinMeetingViewController";
NSString* const NOTES_VC_NIB_NAME = @"iWinNoteListViewController";
NSString* const TASK_VC_NIB_NAME = @"iWinTaskListViewController";
NSString* const SETTINGS_VC_NIB_NAME = @"iWinViewAndChangeSettingsViewController";
NSString* const PROFILE_VC_NIB_NAME = @"iWinViewProfileViewController";
NSString* const ID_KEY = @"id";
NSString* const MEETING_KEY = @"meeting";
NSString* const TITLE_KEY = @"title";
NSString* const MEETING_HEADER = @"Meeting";
NSString* const CUSTOM_SUBTITLED_CELL = @"CustomSubtitledCell";
NSString* const LOCATION_KEY = @"location";
NSString* const DATE_TIME_KEY = @"datetime";
NSString* const DESCRIPTION_KEY = @"description";
NSString* const CONFIRM_DELETE_TITLE = @"Confirm Delete";
NSString* const NO_DELETE_OPTION = @"No, just kidding!";
NSString* const YES_DELETE_OPTION = @"Yes, please";
NSString* const MEETING_URL = @"%@/Meeting/%d";
NSString* const MEETING_NOT_FOUND_MESSAGE = @"Meetings not found";
NSString* const END_DATE_TIME_KEY = @"endDatetime";
NSString* const USER_ID_URL = @"%@/User/%d";
NSString* const ATTENDANCE_KEY = @"attendance";
NSString* const DELETE_MEETING_MESSAGE = @"Are you sure you want to delete this meeting?";
NSString* const VIEW_AND_ADD_AGENDA_NIB = @"iWinViewAndAddViewController";
NSString* const ADD_USERS_NIB = @"iWinAddUsersViewController";
NSString* const NAME_KEY = @"name";
NSString* const TYPE_KEY = @"type";
NSString* const SCHEDULE_KEY = @"schedule";
NSString* const DATE_TIME_START = @"datetimeStart";
NSString* const DATE_TIME_END = @"datetimeEnd";
NSString* const MEETING_ID_KEY = @"meetingID";
NSString* const EMPTY_STRING = @"";
NSString* const SAVE_BUTTON = @"Save";
NSString* const AGENDA_URL = @"%@/Agenda/%d";
NSString* const CONTENT_KEY = @"content";
NSString* const AGENDA_ITEM_NIB = @"iWinAgendaItemViewController";
NSString* const USER_KEY = @"user";
NSString* const AGENDA_LIST_URL = @"%@/Agenda/";
NSString* const AGENDA_ID_KEY = @"agendaID";

@end
