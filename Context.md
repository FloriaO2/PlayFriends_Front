
# PlayFriends 프론트엔드 개발 가이드

## 1. 애플리케이션 개요

PlayFriends는 사용자가 친구들과 함께 놀 계획을 세우는 데 도움을 주는 애플리케이션입니다. 사용자들은 그룹(방)을 만들고 참여할 수 있으며, 각자의 음식 및 놀이 선호도를 설정할 수 있습니다. 백엔드는 이러한 선호도를 기반으로 그룹 멤버들에게 최적화된 놀이 스케줄(예: 오후 1시 맛집, 오후 2시 카페 등)을 추천해주는 기능을 제공합니다.

## 2. 핵심 기능 (프론트엔드 관점)

- **사용자 인증:** 회원가입, 로그인, 로그아웃 기능을 구현합니다. 로그인 시 받은 토큰은 이후 모든 API 요청에 사용해야 합니다.
- **그룹 관리:**
    - 그룹 생성, 조회, 수정, 삭제 기능을 구현합니다.
    - 사용자는 그룹에 참여하거나 나갈 수 있습니다.
    - 그룹장은 그룹 정보를 수정하거나 그룹을 삭제할 수 있습니다.
- **선호도 설정:**
    - 사용자는 자신의 프로필 페이지에서 음식 및 놀이 관련 선호도를 설정하고 수정할 수 있습니다.
    - 선호도 데이터는 스케줄 추천의 기반이 되므로, 사용자 친화적인 UI를 제공하는 것이 중요합니다.
- **스케줄 추천 및 확정:**
    - 그룹장은 그룹 멤버들의 통합된 선호도를 바탕으로 놀이 카테고리를 추천받을 수 있습니다.
    - 추천받은 카테고리를 기반으로 구체적인 시간과 장소가 포함된 스케줄 제안을 생성할 수 있습니다.
    - 제안된 스케줄 중 하나를 최종적으로 확정하고 그룹에 저장할 수 있습니다.
- **마이페이지:**
    - 사용자는 자신이 속한 그룹 목록을 확인할 수 있습니다.
    - 자신의 프로필 정보(이름, 선호도 등)를 수정할 수 있습니다.

## 3. API 엔드포인트 상세 가이드

- **Base URL:** `/api/v1`
- **인증:** 로그인이 필요한 모든 API는 HTTP Header에 `Authorization: Bearer <ACCESS_TOKEN>`을 포함하여 요청해야 합니다.

---

### 3.1. 사용자 (Users)

- `POST /token` **(로그인)**
    - **설명:** 사용자 ID와 비밀번호로 로그인하여 액세스 토큰을 발급받습니다.
    - **요청:** `LoginRequest` 스키마 (userid, password)
    - **응답:** `Token` 스키마 (access_token, token_type)
    - **활용:** 로그인 성공 후 받은 `access_token`을 클라이언트(예: localStorage, Redux store)에 저장하고, 이후 인증이 필요한 모든 요청의 헤더에 담아 보내야 합니다.

- `POST /create_user/` **(회원가입)**
    - **설명:** 새로운 사용자를 생성합니다.
    - **요청:** `UserCreate` 스키마 (userid, username, password)
    - **응답:** `User` 스키마 (생성된 사용자 정보)

- `GET /users/me` **(내 정보 조회)**
    - **설명:** 현재 로그인된 사용자의 상세 정보를 가져옵니다.
    - **응답:** `User` 스키마
    - **활용:** 마이페이지 등에서 사용자 정보를 표시할 때 사용합니다.

- `PUT /users/me` **(내 정보 수정)**
    - **설명:** 현재 로그인된 사용자의 이름, 음식/놀이 선호도를 수정합니다.
    - **요청:** `UserUpdate` 스키마
    - **응답:** `User` 스키마 (수정된 사용자 정보)

- `PUT /users/preferences` **(선호도 수정)**
    - **설명:** 사용자의 음식 및 놀이 선호도를 상세하게 업데이트합니다.
    - **요청:** `food_preferences`, `play_preferences` 객체를 포함하는 Body
    - **활용:** 선호도 설정 페이지에서 사용자가 슬라이더나 선택지를 통해 값을 조절하면, 이 API를 호출하여 서버에 저장합니다.

- `GET /users/{user_id}/groups` **(사용자가 속한 그룹 목록 조회)**
    - **설명:** 특정 사용자가 속해있는 모든 그룹의 목록을 가져옵니다.
    - **응답:** `GroupList` 스키마
    - **활용:** 마이페이지에서 "내 그룹 목록"을 보여줄 때 사용합니다.

---

### 3.2. 그룹 (Groups)

- `POST /groups/` **(그룹 생성)**
    - **설명:** 새로운 그룹을 생성합니다.
    - **요청:** `GroupCreate` 스키마 (groupname, starttime, endtime)
    - **응답:** `GroupResponse` 스키마 (생성된 그룹 정보)
    - **참고:** 그룹 생성자가 자동으로 그룹의 `owner`가 됩니다.

- `GET /groups/` **(모든 그룹 조회)**
    - **설명:** 현재 활성화된 모든 그룹의 목록을 가져옵니다.
    - **응답:** `GroupList` 스키마
    - **활용:** 사용자가 참여할 그룹을 탐색하는 "그룹 찾기"와 같은 페이지에서 사용합니다.

- `GET /groups/{group_id}` **(특정 그룹 상세 조회)**
    - **설명:** 특정 그룹의 상세 정보를 가져옵니다. 멤버 목록, 확정된 스케줄 등을 포함합니다.
    - **응답:** `GroupDetailResponse` 스키마
    - **활용:** 그룹 상세 페이지에서 사용합니다.

- `PUT /groups/{group_id}` **(그룹 정보 수정)**
    - **설명:** 그룹의 정보를 수정합니다. 그룹장만 호출할 수 있습니다.
    - **요청:** `GroupUpdate` 스키마

- `DELETE /groups/{group_id}` **(그룹 삭제)**
    - **설명:** 그룹을 삭제합니다. 그룹장만 호출할 수 있습니다.

- `POST /groups/{group_id}/join` **(그룹 참여)**
    - **설명:** 사용자가 특정 그룹에 참여합니다.
    - **응답:** 성공 메시지

- `DELETE /groups/{group_id}/leave` **(그룹 탈퇴)**
    - **설명:** 사용자가 특정 그룹에서 나갑니다.
    - **응답:** 성공 메시지

---

### 3.3. 스케줄 (Schedules)

- `POST /groups/{group_id}/recommend-categories` **(놀이 카테고리 추천)**
    - **설명:** 그룹 멤버들의 선호도를 종합하여 놀이 카테고리를 추천받습니다. 그룹장만 호출할 수 있습니다.
    - **응답:** `CategoryListResponse` 스키마 (추천 카테고리 목록)
    - **활용:** 그룹장이 스케줄 생성을 시작할 때, 이 API를 통해 추천받은 카테고리들을 UI에 보여주고 선택하게 할 수 있습니다.

- `POST /groups/{group_id}/schedules` **(스케줄 제안 생성)**
    - **설명:** 사용자가 선택한 카테고리들을 바탕으로 구체적인 활동이 포함된 여러 스케줄 안을 제안받습니다.
    - **요청:** `categories` (문자열 배열)
    - **응답:** `ListScheduleResponse` 스키마 (여러 개의 스케줄 제안 목록)
    - **활용:** 추천받은 카테고리 중 몇 개를 선택하여 이 API를 호출하면, 구체적인 시간과 장소가 포함된 여러 스케줄 옵션을 사용자에게 보여줄 수 있습니다.

- `POST /groups/schedule` **(스케줄 확정)**
    - **설명:** 제안받은 스케줄 안 중 하나를 최종적으로 확정하여 그룹에 저장합니다.
    - **요청:** `ScheduleSuggestion-Input` 스키마 (group_id, scheduled_activities)
    - **응답:** `GroupResponse` 스키마 (스케줄이 확정된 그룹 정보)
    - **활용:** 사용자가 여러 스케줄 제안 중 하나를 선택하면, 해당 스케줄 정보를 이 API로 보내 그룹의 공식 스케줄로 저장합니다.

## 4. 주요 데이터 스키마 설명

- **User:** 사용자 정보. `userid`, `username`, `food_preferences`, `play_preferences` 등을 포함합니다.
- **Group:** 그룹 정보. `groupname`, `starttime`, `endtime`, `owner_id`, `member_ids`, `schedule` 등을 포함합니다.
- **FoodPreferences:** 음식 선호도. `ingredients`, `tastes`, `cooking_methods`, `cuisine_types` 각각에 대해 선호도 점수(`score`)를 매깁니다.
- **PlayPreferences:** 놀이 선호도. `crowd_level`(붐비는 정도), `activeness_level`(활동성) 등 다양한 항목에 대해 선호도 점수(`score`)를 매깁니다.
- **ScheduledActivity:** 개별 활동 정보. `name`, `category`, `start_time`, `end_time`, `location` (좌표) 등을 포함합니다.
- **ScheduleSuggestion:** 스케줄 제안. 여러 개의 `ScheduledActivity`로 구성됩니다.

## 5. 추천 개발 순서

1.  **인증 구현:** 로그인, 회원가입 페이지를 만들고 토큰 기반 인증 로직을 구현합니다.
2.  **메인/그룹 탐색 페이지:** 전체 그룹 목록을 보여주고, 사용자가 그룹을 생성하거나 참여할 수 있는 기능을 구현합니다.
3.  **마이페이지:** 내 정보를 확인하고, 선호도를 설정/수정하는 페이지를 구현합니다.
4.  **그룹 상세 페이지:** 그룹의 상세 정보, 멤버 목록, 확정된 스케줄을 보여줍니다. 그룹장에게는 정보 수정/삭제, 카테고리 추천 버튼 등을 추가로 보여줍니다.
5.  **스케줄 생성/확정 플로우:**
    - (그룹장) 카테고리 추천을 받아 화면에 표시합니다.
    - (그룹장) 카테고리를 선택하여 스케줄 제안을 요청하고, 여러 제안을 화면에 비교하여 보여줍니다.
    - (그룹장) 최종 스케줄을 선택하여 확정합니다.

이 가이드가 프론트엔드 AI의 원활한 개발에 도움이 되기를 바랍니다. 추가적인 질문이나 백엔드 수정 요청이 있다면 언제든지 알려주세요.

## 6. API 명세
{"openapi":"3.1.0","info":{"title":"FastAPI","version":"0.1.0"},"paths":{"/":{"get":{"tags":["example"],"summary":"Read Root","operationId":"read_root__get","responses":{"200":{"description":"Successful Response","content":{"application/json":{"schema":{}}}}}}},"/number/{number}":{"get":{"tags":["example"],"summary":"Read Number","operationId":"read_number_number__number__get","parameters":[{"name":"number","in":"path","required":true,"schema":{"type":"integer","title":"Number"}}],"responses":{"200":{"description":"Successful Response","content":{"application/json":{"schema":{}}}},"422":{"description":"Validation Error","content":{"application/json":{"schema":{"$ref":"#/components/schemas/HTTPValidationError"}}}}}}},"/database":{"get":{"tags":["example"],"summary":"Get Database Info","operationId":"get_database_info_database_get","responses":{"200":{"description":"Successful Response","content":{"application/json":{"schema":{}}}}}}},"/api/v1/token":{"post":{"tags":["users"],"summary":"Login For Access Token","operationId":"login_for_access_token_api_v1_token_post","requestBody":{"content":{"application/json":{"schema":{"$ref":"#/components/schemas/LoginRequest"}}},"required":true},"responses":{"200":{"description":"Successful Response","content":{"application/json":{"schema":{"$ref":"#/components/schemas/Token"}}}},"422":{"description":"Validation Error","content":{"application/json":{"schema":{"$ref":"#/components/schemas/HTTPValidationError"}}}}}}},"/api/v1/logout":{"post":{"tags":["users"],"summary":"Logout","operationId":"logout_api_v1_logout_post","responses":{"200":{"description":"Successful Response","content":{"application/json":{"schema":{}}}}},"security":[{"OAuth2PasswordBearer":[]}]}},"/api/v1/create_user/":{"post":{"tags":["users"],"summary":"Create User","description":"Create a new user.","operationId":"create_user_api_v1_create_user__post","requestBody":{"content":{"application/json":{"schema":{"$ref":"#/components/schemas/UserCreate"}}},"required":true},"responses":{"201":{"description":"Successful Response","content":{"application/json":{"schema":{"$ref":"#/components/schemas/User"}}}},"422":{"description":"Validation Error","content":{"application/json":{"schema":{"$ref":"#/components/schemas/HTTPValidationError"}}}}}}},"/api/v1/users/me":{"get":{"tags":["users"],"summary":"Read Users Me","operationId":"read_users_me_api_v1_users_me_get","responses":{"200":{"description":"Successful Response","content":{"application/json":{"schema":{"$ref":"#/components/schemas/User"}}}}},"security":[{"OAuth2PasswordBearer":[]}]},"put":{"tags":["users"],"summary":"Update User Me","operationId":"update_user_me_api_v1_users_me_put","requestBody":{"content":{"application/json":{"schema":{"$ref":"#/components/schemas/UserUpdate"}}},"required":true},"responses":{"200":{"description":"Successful Response","content":{"application/json":{"schema":{"$ref":"#/components/schemas/User"}}}},"422":{"description":"Validation Error","content":{"application/json":{"schema":{"$ref":"#/components/schemas/HTTPValidationError"}}}}},"security":[{"OAuth2PasswordBearer":[]}]}},"/api/v1/users/{user_id}/groups":{"get":{"tags":["users"],"summary":"Get Groups By User","operationId":"get_groups_by_user_api_v1_users__user_id__groups_get","security":[{"OAuth2PasswordBearer":[]}],"parameters":[{"name":"user_id","in":"path","required":true,"schema":{"type":"string","title":"User Id"}}],"responses":{"200":{"description":"Successful Response","content":{"application/json":{"schema":{"$ref":"#/components/schemas/GroupList"}}}},"422":{"description":"Validation Error","content":{"application/json":{"schema":{"$ref":"#/components/schemas/HTTPValidationError"}}}}}}},"/api/v1/users/{user_id}/groups/{group_id}":{"get":{"tags":["users"],"summary":"Get Group By User","operationId":"get_group_by_user_api_v1_users__user_id__groups__group_id__get","security":[{"OAuth2PasswordBearer":[]}],"parameters":[{"name":"user_id","in":"path","required":true,"schema":{"type":"string","title":"User Id"}},{"name":"group_id","in":"path","required":true,"schema":{"type":"string","title":"Group Id"}}],"responses":{"200":{"description":"Successful Response","content":{"application/json":{"schema":{"$ref":"#/components/schemas/GroupDetailResponse"}}}},"422":{"description":"Validation Error","content":{"application/json":{"schema":{"$ref":"#/components/schemas/HTTPValidationError"}}}}}}},"/api/v1/users/preferences":{"put":{"tags":["users"],"summary":"Update Preferences","description":"Update user's food and play preferences.","operationId":"update_preferences_api_v1_users_preferences_put","requestBody":{"content":{"application/json":{"schema":{"$ref":"#/components/schemas/Body_update_preferences_api_v1_users_preferences_put"}}},"required":true},"responses":{"200":{"description":"Successful Response","content":{"application/json":{"schema":{}}}},"422":{"description":"Validation Error","content":{"application/json":{"schema":{"$ref":"#/components/schemas/HTTPValidationError"}}}}},"security":[{"OAuth2PasswordBearer":[]}]}},"/api/v1/groups/":{"get":{"tags":["groups"],"summary":"Get All Groups","description":"Get all groups.","operationId":"get_all_groups_api_v1_groups__get","responses":{"200":{"description":"Successful Response","content":{"application/json":{"schema":{"$ref":"#/components/schemas/GroupList"}}}}}},"post":{"tags":["groups"],"summary":"Create Group","description":"Create a new group.","operationId":"create_group_api_v1_groups__post","requestBody":{"content":{"application/json":{"schema":{"$ref":"#/components/schemas/GroupCreate"}}},"required":true},"responses":{"201":{"description":"Successful Response","content":{"application/json":{"schema":{"$ref":"#/components/schemas/GroupDetailResponse"}}}},"422":{"description":"Validation Error","content":{"application/json":{"schema":{"$ref":"#/components/schemas/HTTPValidationError"}}}}},"security":[{"OAuth2PasswordBearer":[]}]}},"/api/v1/groups/{group_id}":{"get":{"tags":["groups"],"summary":"Get Group","description":"Get a single group by ID.","operationId":"get_group_api_v1_groups__group_id__get","parameters":[{"name":"group_id","in":"path","required":true,"schema":{"type":"string","title":"Group Id"}}],"responses":{"200":{"description":"Successful Response","content":{"application/json":{"schema":{"$ref":"#/components/schemas/GroupDetailResponse"}}}},"422":{"description":"Validation Error","content":{"application/json":{"schema":{"$ref":"#/components/schemas/HTTPValidationError"}}}}}},"put":{"tags":["groups"],"summary":"Update Group","description":"Update a group. Only the owner can update the group.","operationId":"update_group_api_v1_groups__group_id__put","security":[{"OAuth2PasswordBearer":[]}],"parameters":[{"name":"group_id","in":"path","required":true,"schema":{"type":"string","title":"Group Id"}}],"requestBody":{"required":true,"content":{"application/json":{"schema":{"$ref":"#/components/schemas/GroupUpdate"}}}},"responses":{"200":{"description":"Successful Response","content":{"application/json":{"schema":{"$ref":"#/components/schemas/GroupDetailResponse"}}}},"422":{"description":"Validation Error","content":{"application/json":{"schema":{"$ref":"#/components/schemas/HTTPValidationError"}}}}}},"delete":{"tags":["groups"],"summary":"Delete Group","description":"Delete a group. Only the owner can delete the group.","operationId":"delete_group_api_v1_groups__group_id__delete","security":[{"OAuth2PasswordBearer":[]}],"parameters":[{"name":"group_id","in":"path","required":true,"schema":{"type":"string","title":"Group Id"}}],"responses":{"204":{"description":"Successful Response"},"422":{"description":"Validation Error","content":{"application/json":{"schema":{"$ref":"#/components/schemas/HTTPValidationError"}}}}}}},"/api/v1/groups/{group_id}/join":{"post":{"tags":["groups"],"summary":"Join Group","description":"Join a group.","operationId":"join_group_api_v1_groups__group_id__join_post","security":[{"OAuth2PasswordBearer":[]}],"parameters":[{"name":"group_id","in":"path","required":true,"schema":{"type":"string","title":"Group Id"}}],"responses":{"200":{"description":"Successful Response","content":{"application/json":{"schema":{"$ref":"#/components/schemas/Message"}}}},"422":{"description":"Validation Error","content":{"application/json":{"schema":{"$ref":"#/components/schemas/HTTPValidationError"}}}}}}},"/api/v1/groups/{group_id}/leave":{"delete":{"tags":["groups"],"summary":"Leave Group","description":"Leave a group.","operationId":"leave_group_api_v1_groups__group_id__leave_delete","security":[{"OAuth2PasswordBearer":[]}],"parameters":[{"name":"group_id","in":"path","required":true,"schema":{"type":"string","title":"Group Id"}}],"responses":{"200":{"description":"Successful Response","content":{"application/json":{"schema":{"$ref":"#/components/schemas/Message"}}}},"422":{"description":"Validation Error","content":{"application/json":{"schema":{"$ref":"#/components/schemas/HTTPValidationError"}}}}}}},"/api/v1/groups/{group_id}/recommend-categories":{"post":{"tags":["groups"],"summary":"Recommend Categories","description":"Recommend play categories for a group based on member preferences.\nOnly the group owner can request recommendations.","operationId":"recommend_categories_api_v1_groups__group_id__recommend_categories_post","security":[{"OAuth2PasswordBearer":[]}],"parameters":[{"name":"group_id","in":"path","required":true,"schema":{"type":"string","title":"Group Id"}}],"responses":{"200":{"description":"Successful Response","content":{"application/json":{"schema":{"$ref":"#/components/schemas/CategoryListResponse"}}}},"422":{"description":"Validation Error","content":{"application/json":{"schema":{"$ref":"#/components/schemas/HTTPValidationError"}}}}}}},"/api/v1/groups/{group_id}/schedules":{"post":{"tags":["groups"],"summary":"Create Schedule","description":"Create a schedule for a group based on selected categories.\nAny member of the group can create a schedule.","operationId":"create_schedule_api_v1_groups__group_id__schedules_post","security":[{"OAuth2PasswordBearer":[]}],"parameters":[{"name":"group_id","in":"path","required":true,"schema":{"type":"string","title":"Group Id"}}],"requestBody":{"required":true,"content":{"application/json":{"schema":{"$ref":"#/components/schemas/Body_create_schedule_api_v1_groups__group_id__schedules_post"}}}},"responses":{"201":{"description":"Successful Response","content":{"application/json":{"schema":{"$ref":"#/components/schemas/ListScheduleResponse"}}}},"422":{"description":"Validation Error","content":{"application/json":{"schema":{"$ref":"#/components/schemas/HTTPValidationError"}}}}}}},"/api/v1/groups/schedule":{"post":{"tags":["groups"],"summary":"그룹 스케줄 확정 및 저장","description":"제안된 스케줄을 그룹에 확정하고, 장소 간의 이동 거리를 계산하여 저장합니다.","operationId":"confirm_schedule_api_v1_groups_schedule_post","requestBody":{"content":{"application/json":{"schema":{"$ref":"#/components/schemas/ScheduleSuggestion-Input"}}},"required":true},"responses":{"200":{"description":"Successful Response","content":{"application/json":{"schema":{"$ref":"#/components/schemas/GroupDetailResponse"}}}},"422":{"description":"Validation Error","content":{"application/json":{"schema":{"$ref":"#/components/schemas/HTTPValidationError"}}}}}}}},"components":{"schemas":{"Body_create_schedule_api_v1_groups__group_id__schedules_post":{"properties":{"categories":{"items":{"type":"string"},"type":"array","title":"Categories"}},"type":"object","required":["categories"],"title":"Body_create_schedule_api_v1_groups__group_id__schedules_post"},"Body_update_preferences_api_v1_users_preferences_put":{"properties":{"food_preferences":{"$ref":"#/components/schemas/FoodPreferences-Input"},"play_preferences":{"$ref":"#/components/schemas/PlayPreferences"}},"type":"object","required":["food_preferences","play_preferences"],"title":"Body_update_preferences_api_v1_users_preferences_put"},"CategoryListResponse":{"properties":{"categories":{"items":{"type":"string"},"type":"array","title":"Categories"}},"type":"object","required":["categories"],"title":"CategoryListResponse"},"CookingMethodPreference":{"properties":{"name":{"$ref":"#/components/schemas/FoodCookingMethod"},"score":{"type":"number","maximum":1.0,"minimum":-1.0,"title":"Score","default":0.0}},"type":"object","required":["name"],"title":"CookingMethodPreference"},"CuisineTypePreference":{"properties":{"name":{"$ref":"#/components/schemas/FoodCuisineType"},"score":{"type":"number","maximum":1.0,"minimum":-1.0,"title":"Score","default":0.0}},"type":"object","required":["name"],"title":"CuisineTypePreference"},"FoodCookingMethod":{"type":"string","enum":["국물","구이","찜/찌개","볶음","튀김","날것","음료"],"title":"FoodCookingMethod"},"FoodCuisineType":{"type":"string","enum":["한식","중식","일식","양식","동남아식"],"title":"FoodCuisineType"},"FoodIngredient":{"type":"string","enum":["고기","채소","생선","우유","계란","밀가루"],"title":"FoodIngredient"},"FoodPreferences-Input":{"properties":{"ingredients":{"items":{"$ref":"#/components/schemas/IngredientPreference"},"type":"array","title":"Ingredients"},"tastes":{"items":{"$ref":"#/components/schemas/TastePreference"},"type":"array","title":"Tastes"},"cooking_methods":{"items":{"$ref":"#/components/schemas/CookingMethodPreference"},"type":"array","title":"Cooking Methods"},"cuisine_types":{"items":{"$ref":"#/components/schemas/CuisineTypePreference"},"type":"array","title":"Cuisine Types"}},"type":"object","title":"FoodPreferences"},"FoodPreferences-Output":{"properties":{"ingredients":{"items":{"$ref":"#/components/schemas/IngredientPreference"},"type":"array","title":"Ingredients"},"tastes":{"items":{"$ref":"#/components/schemas/TastePreference"},"type":"array","title":"Tastes"},"cooking_methods":{"items":{"$ref":"#/components/schemas/CookingMethodPreference"},"type":"array","title":"Cooking Methods"},"cuisine_types":{"items":{"$ref":"#/components/schemas/CuisineTypePreference"},"type":"array","title":"Cuisine Types"}},"type":"object","title":"FoodPreferences"},"FoodTaste":{"type":"string","enum":["매운","느끼한","단","짠","쓴","신"],"title":"FoodTaste"},"GeoJson":{"properties":{"type":{"type":"string","title":"Type","default":"Point"},"coordinates":{"items":{"type":"number"},"type":"array","title":"Coordinates","description":"[경도, 위도] 순서"}},"type":"object","required":["coordinates"],"title":"GeoJson"},"GroupCreate":{"properties":{"groupname":{"type":"string","title":"Groupname"},"starttime":{"type":"string","format":"date-time","title":"Starttime"},"endtime":{"anyOf":[{"type":"string","format":"date-time"},{"type":"null"}],"title":"Endtime"}},"type":"object","required":["groupname","starttime","endtime"],"title":"GroupCreate"},"GroupDetailResponse":{"properties":{"_id":{"type":"string","title":"Id"},"groupname":{"type":"string","title":"Groupname","description":"그룹 이름"},"starttime":{"type":"string","format":"date-time","title":"Starttime","description":"시작 시간"},"endtime":{"anyOf":[{"type":"string","format":"date-time"},{"type":"null"}],"title":"Endtime","description":"종료 시간"},"is_active":{"type":"boolean","title":"Is Active","default":true},"owner_id":{"type":"string","title":"Owner Id","description":"방장 _ID"},"member_ids":{"items":{"type":"string"},"type":"array","title":"Member Ids","description":"참여자 _ID 목록","default":[]},"food_preferences":{"anyOf":[{"$ref":"#/components/schemas/FoodPreferences-Output"},{"type":"null"}],"description":"그룹의 통합 음식 선호도"},"play_preferences":{"anyOf":[{"$ref":"#/components/schemas/PlayPreferences"},{"type":"null"}],"description":"그룹의 통합 놀이 선호도"},"schedule":{"anyOf":[{"items":{"$ref":"#/components/schemas/ScheduledActivity"},"type":"array"},{"type":"null"}],"title":"Schedule","description":"확정된 스케줄"},"distances_km":{"anyOf":[{"items":{"type":"number"},"type":"array"},{"type":"null"}],"title":"Distances Km","description":"스케줄 장소 간 이동 거리 목록 (km)"},"members":{"items":{"$ref":"#/components/schemas/GroupMember"},"type":"array","title":"Members","description":"참여자 이름 목록","default":[]}},"type":"object","required":["groupname","starttime","owner_id"],"title":"GroupDetailResponse"},"GroupList":{"properties":{"groups":{"items":{"$ref":"#/components/schemas/GroupDetailResponse"},"type":"array","title":"Groups"}},"type":"object","required":["groups"],"title":"GroupList"},"GroupMember":{"properties":{"id":{"type":"string","title":"Id"},"name":{"type":"string","title":"Name"}},"type":"object","required":["id","name"],"title":"GroupMember"},"GroupUpdate":{"properties":{"groupname":{"anyOf":[{"type":"string"},{"type":"null"}],"title":"Groupname"},"starttime":{"anyOf":[{"type":"string","format":"date-time"},{"type":"null"}],"title":"Starttime"},"endtime":{"anyOf":[{"type":"string","format":"date-time"},{"type":"null"}],"title":"Endtime"},"is_active":{"anyOf":[{"type":"boolean"},{"type":"null"}],"title":"Is Active"},"member_ids":{"anyOf":[{"items":{"type":"string"},"type":"array"},{"type":"null"}],"title":"Member Ids"},"schedule":{"anyOf":[{"items":{"$ref":"#/components/schemas/ScheduledActivity"},"type":"array"},{"type":"null"}],"title":"Schedule"},"distances_km":{"anyOf":[{"items":{"type":"number"},"type":"array"},{"type":"null"}],"title":"Distances Km"}},"type":"object","title":"GroupUpdate"},"HTTPValidationError":{"properties":{"detail":{"items":{"$ref":"#/components/schemas/ValidationError"},"type":"array","title":"Detail"}},"type":"object","title":"HTTPValidationError"},"IngredientPreference":{"properties":{"name":{"$ref":"#/components/schemas/FoodIngredient"},"score":{"type":"number","maximum":1.0,"minimum":-1.0,"title":"Score","default":0.0}},"type":"object","required":["name"],"title":"IngredientPreference"},"ListScheduleResponse":{"properties":{"schedules":{"items":{"$ref":"#/components/schemas/ScheduleSuggestion-Output"},"type":"array","title":"Schedules"}},"type":"object","required":["schedules"],"title":"ListScheduleResponse"},"LoginRequest":{"properties":{"userid":{"type":"string","title":"Userid"},"password":{"type":"string","title":"Password"},"auto_login":{"type":"boolean","title":"Auto Login","default":false}},"type":"object","required":["userid","password"],"title":"LoginRequest"},"Message":{"properties":{"message":{"type":"string","title":"Message"}},"type":"object","required":["message"],"title":"Message"},"PlayPreferences":{"properties":{"crowd_level":{"type":"number","maximum":1.0,"minimum":-1.0,"title":"Crowd Level","description":"붐비는 정도 (-1: 조용, 1: 붐빔)","default":0.0},"activeness_level":{"type":"number","maximum":1.0,"minimum":-1.0,"title":"Activeness Level","description":"활동성 (-1: 관람형, 1: 체험형)","default":0.0},"trend_level":{"type":"number","maximum":1.0,"minimum":-1.0,"title":"Trend Level","description":"유행 민감도 (-1: 비유행, 1: 유행)","default":0.0},"planning_level":{"type":"number","maximum":1.0,"minimum":-1.0,"title":"Planning Level","description":"계획성 (-1: 즉흥, 1: 계획)","default":0.0},"location_preference":{"type":"number","maximum":1.0,"minimum":-1.0,"title":"Location Preference","description":"장소 (-1: 실외, 1: 실내)","default":0.0},"vibe_level":{"type":"number","maximum":1.0,"minimum":-1.0,"title":"Vibe Level","description":"분위기 (-1: 안정, 1: 도파민 추구)","default":0.0}},"type":"object","title":"PlayPreferences"},"ScheduleSuggestion-Input":{"properties":{"group_id":{"type":"string","title":"Group Id","description":"그룹의 ID"},"scheduled_activities":{"items":{"$ref":"#/components/schemas/ScheduledActivity"},"type":"array","title":"Scheduled Activities","description":"스케줄된 활동 목록"}},"type":"object","required":["group_id","scheduled_activities"],"title":"ScheduleSuggestion"},"ScheduleSuggestion-Output":{"properties":{"group_id":{"type":"string","title":"Group Id","description":"그룹의 ID"},"scheduled_activities":{"items":{"$ref":"#/components/schemas/ScheduledActivity"},"type":"array","title":"Scheduled Activities","description":"스케줄된 활동 목록"}},"type":"object","required":["group_id","scheduled_activities"],"title":"ScheduleSuggestion"},"ScheduledActivity":{"properties":{"name":{"type":"string","title":"Name","description":"활동의 이름"},"category":{"type":"string","title":"Category","description":"카테고리 이름"},"start_time":{"type":"string","format":"date-time","title":"Start Time","description":"활동 시작 시간"},"end_time":{"type":"string","format":"date-time","title":"End Time","description":"활동 종료 시간"},"location":{"anyOf":[{"$ref":"#/components/schemas/GeoJson"},{"type":"null"}],"description":"활동 장소"}},"type":"object","required":["name","category","start_time","end_time"],"title":"ScheduledActivity"},"TastePreference":{"properties":{"name":{"$ref":"#/components/schemas/FoodTaste"},"score":{"type":"number","maximum":1.0,"minimum":-1.0,"title":"Score","default":0.0}},"type":"object","required":["name"],"title":"TastePreference"},"Token":{"properties":{"access_token":{"type":"string","title":"Access Token"},"token_type":{"type":"string","title":"Token Type"}},"type":"object","required":["access_token","token_type"],"title":"Token"},"User":{"properties":{"userid":{"type":"string","title":"Userid","description":"고유한 사용자 ID"},"username":{"type":"string","title":"Username","description":"사용자 이름"},"is_active":{"type":"boolean","title":"Is Active","default":true},"_id":{"type":"string","title":"Id"},"food_preferences":{"$ref":"#/components/schemas/FoodPreferences-Output"},"play_preferences":{"$ref":"#/components/schemas/PlayPreferences"}},"type":"object","required":["userid","username","_id"],"title":"User"},"UserCreate":{"properties":{"userid":{"type":"string","title":"Userid","description":"고유한 사용자 ID"},"username":{"type":"string","title":"Username","description":"사용자 이름"},"is_active":{"type":"boolean","title":"Is Active","default":true},"password":{"type":"string","minLength":8,"title":"Password","description":"비밀번호 (최소 8자 이상)"}},"type":"object","required":["userid","username","password"],"title":"UserCreate"},"UserUpdate":{"properties":{"username":{"anyOf":[{"type":"string"},{"type":"null"}],"title":"Username","description":"사용자 이름"},"food_preferences":{"anyOf":[{"$ref":"#/components/schemas/FoodPreferences-Input"},{"type":"null"}]},"play_preferences":{"anyOf":[{"$ref":"#/components/schemas/PlayPreferences"},{"type":"null"}]}},"type":"object","title":"UserUpdate"},"ValidationError":{"properties":{"loc":{"items":{"anyOf":[{"type":"string"},{"type":"integer"}]},"type":"array","title":"Location"},"msg":{"type":"string","title":"Message"},"type":{"type":"string","title":"Error Type"}},"type":"object","required":["loc","msg","type"],"title":"ValidationError"}},"securitySchemes":{"OAuth2PasswordBearer":{"type":"oauth2","flows":{"password":{"scopes":{},"tokenUrl":"/api/v1/token"}}}}}}