<?php

error_reporting(E_ALL);

use Phalcon\Loader;
use Phalcon\Mvc\Micro;
use Phalcon\DI\FactoryDefault;
use Phalcon\Http\Response;
use Phalcon\Db\Adapter\Pdo\Mysql as PdoMysql;

require_once 'include/PassHash.php';
try {
    $config = include __DIR__ . '/config/config.php';
    /**
     * Registering an autoloader
     */
    $loader = new Loader();
    $loader->registerDirs(
            array(
                $config->application->modelsDir
            )
    )->register();

    $di = new FactoryDefault();


    /**
     * Database connection is created based in the parameters defined in the configuration file
     */
    $di->set('db', function () use ($config) {
        return new PdoMysql(
                array(
            "host" => $config->database->host,
            "username" => $config->database->username,
            "password" => $config->database->password,
            "dbname" => $config->database->name
                )
        );
    });

    /**
     * Starting the application
     */
    $app = new Micro($di);


    /**
     * Add your routes here
     */
    /**
     * **************************************************************** TRAININGS TABLE ********************************************************** 
     * *
     * ****************************************************************************************************************************************
     */
	 
	 // Retrieves all trainings - view version

    $app->get('/trainings/view', function () use($app) {

        $query = "SELECT * FROM users_view";
        $trainings = $app->modelsManager->executeQuery($query);
        $response = array();
		if ($trainings->getFirst()) {
		$response[] = array(
                'error' => FALSE,
				'status' => 'Downloading successfull!'
            );
        foreach ($trainings as $training) {
            //print_r($training);
            $response[] = array(
                'name' => utf8_encode($training->name),
                'user_id' => utf8_encode($training->id),
                'tid' => utf8_encode($training->tid),
                'description' => utf8_encode($training->description),
                'distance' => utf8_encode($training->distance),
                'place' => utf8_encode($training->place),
                'time_of_training' => utf8_encode($training->time_of_training),
            );
			}
		} else {
			$response[] = array(
                'error' => TRUE,
				'error_msg' => 'Oops! No trainings found!'
            );
        }

        echo json_encode($response);
    });
	
    // Retrieves all trainings

    $app->get('/trainings', function () use($app) {

        $query = "SELECT Users.name AS name, Users.id AS id, Trainings.tid AS tid, Trainings.description AS description, Trainings.distance AS distance, Trainings.place AS place, Trainings.time_of_training AS time_of_training FROM Trainings LEFT JOIN Users ON Trainings.user_id = Users.id WHERE time_of_training > NOW() ORDER BY time_of_training ASC";
        $trainings = $app->modelsManager->executeQuery($query);
        $response = array();
		if ($trainings->getFirst()) {
		$response[] = array(
                'error' => FALSE,
				'status' => 'Downloading successfull!'
            );
        foreach ($trainings as $training) {
            //print_r($training);
            $response[] = array(
                'name' => utf8_encode($training->name),
                'user_id' => utf8_encode($training->id),
                'tid' => utf8_encode($training->tid),
                'description' => utf8_encode($training->description),
                'distance' => utf8_encode($training->distance),
                'place' => utf8_encode($training->place),
                'time_of_training' => utf8_encode($training->time_of_training),
            );
			}
		} else {
			$response[] = array(
                'error' => TRUE,
				'error_msg' => 'Oops! No trainings found!'
            );
        }

        echo json_encode($response);
    });


// Retrieves trainings based on primary key
    $app->get('/trainings/{id:[0-9]+}', function ($id) use($app) {

        $query = "SELECT Users.name AS name, Users.id AS id, Trainings.tid AS tid, Trainings.description AS description, Trainings.distance AS distance, Trainings.place AS place, Trainings.time_of_training AS time_of_training FROM Trainings LEFT JOIN Users ON Trainings.user_id = Users.id WHERE Trainings.tid = :id:";
        $trainings = $app->modelsManager->executeQuery($query, array(
            'id' => $id
        ));
        $training = $trainings->getFirst();
        $response = array();
        if ($training) {
            $response[] = array(
                'name' => $training->name,
                'user_id' => $training->id,
                'tid' => $training->tid,
                'description' => $training->description,
                'distance' => $training->distance,
                'place' => $training->place,
                'time_of_training' => $training->time_of_training,
            );
        } else {
            $response["error"] = TRUE;
            $response["error_msg"] = "Oops! Training not found.";
        }

        echo json_encode($response);
    });

// Adds a new training

    $app->post('/trainings', function () use ($app) {

        $training = $app->request->getJsonRawBody();
        $query = "INSERT INTO Trainings(user_id, description, place, distance, time_of_training) VALUES ( :user_id:, :description:, :place:, :distance:, :time_of_training:)";

        $status = $app->modelsManager->executeQuery($query, array(
            'user_id' => $training->user_id,
            'description' => $training->description,
            'place' => $training->place,
            'distance' => $training->distance,
            'time_of_training' => $training->time_of_training
        ));

        // Create a response
        $response = new Response();

        // Check if the insertion was successful
        if ($status->success() == true) {

            // Change the HTTP status
            $response->setStatusCode(200, "Created");

            $training->tid = $status->getModel()->tid;

            $response->setJsonContent(
                    array(
						'error'=>FALSE,
                        'status' => 'Training successfully created',
                        'data' => $training
                    )
            );
        } else {

            // Change the HTTP status
            $response->setStatusCode(403, "Forbidden");

            // Send errors to the client
            $errors = array();
            foreach ($status->getMessages() as $message) {
                $errors[] = $message->getMessage();
            }

            $response->setJsonContent(
                    array(
                        'status' => 'An error occurred!',
                        'messages' => $errors
                    )
            );
        }

        return $response;
    });


// Updates trainings based on primary key
    $app->put('/trainings/{id:[0-9]+}', function ($id) use ($app) {

        $training = $app->request->getJsonRawBody();

        $phql = "UPDATE Trainings SET user_id = :user_id:, description = :description:, place = :place:, distance=:distance:, time_of_training = :time_of_training:, updated_at=NOW() WHERE tid = :tid:";
        $status = $app->modelsManager->executeQuery($phql, array(
            'tid' => $id,
            'user_id' => $training->user_id,
            'description' => $training->description,
            'place' => $training->place,
            'distance' => $training->distance,
            'time_of_training' => $training->time_of_training
        ));

        // Create a response
        $response = new Response();

        // Check if the insertion was successful
        if ($status->success() == true) {
            $response->setJsonContent(
                    array(
                        'status' => 'Training updated successfully'
                    )
            );
        } else {

            // Change the HTTP status
            $response->setStatusCode(403, "Forbidden");

            $errors = array();
            foreach ($status->getMessages() as $message) {
                $errors[] = $message->getMessage();
            }

            $response->setJsonContent(
                    array(
                        'status' => 'An error occurred!',
                        'messages' => $errors
                    )
            );
        }

        return $response;
    });

// Deletes trainings based on primary key
    $app->delete('/trainings/{id:[0-9]+}', function ($id) use ($app) {

        $phql = "DELETE FROM Trainings WHERE tid = :id:";
        $status = $app->modelsManager->executeQuery($phql, array(
            'id' => $id
        ));

        // Create a response
        $response = new Response();

        if ($status->success() == true) {
            $response->setJsonContent(
                    array(
                        'status' => 'Training successfully deleted!'
                    )
            );
        } else {

            // Change the HTTP status
            $response->setStatusCode(403, "Forbidden");

            $errors = array();
            foreach ($status->getMessages() as $message) {
                $errors[] = $message->getMessage();
            }

            $response->setJsonContent(
                    array(
                        'status' => 'An error occurred!',
                        'messages' => $errors
                    )
            );
        }

        return $response;
    });


    /**
     * **************************************************************** USERS TABLE ********************************************************** 
     * *
     * ****************************************************************************************************************************************
     */
    //Register a new user
    $app->post('/register', function () use ($app) {

        // Create a response
        $response = new Response();

        $user = $app->request->getJsonRawBody();
        $email = $user->email;
        $password = $user->password;
        $name = $user->name;
        if (!empty($email) && !empty($name) && !empty($password)) {

            $query = "SELECT email FROM Users  WHERE email = :email:";

            $status = $app->modelsManager->executeQuery($query, array(
                'email' => $email
            ));

            $status = $status->getFirst();

            // Check if the insertion was successful
            if ($status) {

                // user exists
                $response->setStatusCode(403, "Forbidden");
                $response->setJsonContent(
                        array(
                            'error' => TRUE,
                            'error_msg' => 'User existed with email: ' . $email
                ));
            } else {
                //user doesn't exist, creating query
                $uuid = (string) uniqid('', true);
                $encrypted_password = PassHash::hash($password);
                $query = "INSERT INTO Users(unique_id, name, email, encrypted_password, created_at) VALUES ( :uuid:, :name:, :email:, :encrypted_password:, NOW())";
                $status = $app->modelsManager->executeQuery($query, array(
                    'uuid' => $uuid,
                    'name' => $name,
                    'email' => $email,
                    'encrypted_password' => $encrypted_password
                ));
                if ($status->success() == true) {
                    $usersReg = "SELECT id, created_at, updated_at, name, email FROM Users WHERE unique_id = :uuid:";
                    $query = $app->modelsManager->executeQuery($usersReg, array(
                        'uuid' => $uuid,
                    ));
                    $userReg = $query->getFirst();
                    //adding new user successfull
                    $response->setStatusCode(201, "Created");
                    $response->setJsonContent(
                            array(
                                'error' => FALSE,
                                'uid' => $uuid,
                                'status' => 'Registration completed!',
                                'user' => array(
                                    'id' => $userReg->id,
                                    'name' => $userReg->name,
                                    'email' => $userReg->email,
                                    'created_at' => $userReg->created_at,
                                    'updated_at' => $userReg->updated_at,
                                )));
                } else {
                    //adding new user failed
                    $errors = array();
                    foreach ($status->getMessages() as $message) {
                        $errors[] = $message->getMessage();
                    }
                    $response->setStatusCode(500, "Internal Server Error");
                    $response->setJsonContent(
                            array(
                                'error' => TRUE,
                                 'error_msg' => 'Internal Server Error'
                            )
                    );
                }
            }
        } else {
            $response->setStatusCode(400, "Bad request");
            $response->setJsonContent(
                    array(
                        'error' => TRUE,
                        'error_msg' => 'Email, name and password cannot be empty!'
                    )
            );
        }

        return $response;
    });





    //Login the user
    $app->post('/login', function () use ($app) {

        // Create a response
        $response = new Response();
        $user = $app->request->getJsonRawBody();
        $password = $user->password;
        $email = $user->email;

        if (!empty($email) && !empty($password)) {

            $query = "SELECT * FROM Users WHERE email = :email:";
            $users = $app->modelsManager->executeQuery($query, array(
                'email' => $email
            ));

            $user = $users->getFirst();
            //$response = array();

            if ($user) {
                //checking password

                if (PassHash::check_password($user->encrypted_password, $password)) {
                    $response->setJsonContent(
                            array(
                                'error' => FALSE,
                                'uid' => $user->unique_id,
                                'user' => array(
                                    'id' => $user->id,
                                    'name' => $user->name,
                                    'email' => $user->email,
                                    'created_at' => $user->created_at,
                                    'updated_at' => $user->updated_at,
                                ))
                    );
                } else {
                    $response->setStatusCode(403, "Forbidden");
                    $response->setJsonContent(
                            array(
                                'error' => TRUE,
                                'error_msg' => 'Incorrect password!'
                            )
                    );
                }
            } else {
                $response->setStatusCode(404, "Not found");
                $response->setJsonContent(
                        array(
                            'error' => TRUE,
                            'error_msg' => 'User with that email not found!'
                        )
                );
            }
        } else {
            $response->setStatusCode(400, "Bad request");
            $response->setJsonContent(
                    array(
                        'error' => TRUE,
                        'error_msg' => 'Email or password is missing!'
                    )
            );
        }

        return $response;
    });

	
	//search for user
	 $app->post('/users/name', function() use($app) {
		 
		$response = array();
        $user = $app->request->getJsonRawBody();
		$user_hint = $user->user_hint;
		if (!empty($user_hint)) {
        $query = "SELECT Users.name AS name, Users.id AS pid FROM Users WHERE Users.name LIKE '%".$user_hint."%'";
        $users = $app->modelsManager->executeQuery($query);
        if ($users->getFirst()) {
		$response[] = array(
                'error' => FALSE,
				'status' => 'Downloading successfull!'
            );
        foreach ($users as $user) {			
            $response[] = array(
                'name' => $user->name,
                'pid' => $user->pid
            );
		}
        } else {
            $response["error"] = TRUE;
            $response["error_msg"] = "Oops! Something went wrong!";
        }
		}else {
			$response["error"] = TRUE;
            $response["error_msg"] = "Oops! Some data is missing!";
		}

        echo json_encode($response);
    });
	 

	
	// Updates user data based on unique key
    $app->put('/users/profiles/id', function () use ($app) {

        $user = $app->request->getJsonRawBody();

        //$phql = "UPDATE Profiles, Users SET Users.name = :name:, Profiles.gender = :gender:, Profiles.date_of_birth = :birth_date: WHERE Users.id = Profiles.user_id AND Users.unique_id = :uid:";
		
		$uid = $user->uid;
		$name = $user->name;
		$birth_date = $user->birth_date;
		$gender = $user->gender;
		$phql_1 = "UPDATE Users SET Users.name = :name: WHERE Users.unique_id = :uid:";
		$phql_2 = "UPDATE Profiles SET  Profiles.gender = :gender:, Profiles.date_of_birth = :birth_date: WHERE (SELECT id FROM Users WHERE unique_id = :uid:) = Profiles.user_id";
        $status_1 = $app->modelsManager->executeQuery($phql_1, array(
		"uid" => $uid,
		"name" => $name,
		));
		$status_2 = $app->modelsManager->executeQuery($phql_2, array(
		"uid" => $uid,
		"birth_date" => $birth_date,
		"gender" => $gender
		));

        // Create a response
        $response = new Response();

        // Check if the insertion was successful
        if ($status_1->success() == true && $status_2->success() == true) {
            $response->setJsonContent(
                    array(
						'error' => FALSE,
                        'status' => 'Profile updated successfully'
                    )
            );
        } else {

            // Change the HTTP status
            $response->setStatusCode(403, "Forbidden");

            $errors = array();
            foreach ($status_1->getMessages() as $message) {
                $errors[] = $message->getMessage();
            }
			foreach ($status_2->getMessages() as $message) {
                $errors[] = $message->getMessage();
            }

            $response->setJsonContent(
                    array(
                        'status' => 'An error occurred!',
                        'messages' => $errors
                    )
            );
        }

        return $response;
    });
	
	
	
    /**
     * **************************************************************** MESSAGES TABLE ********************************************************** 
     * *
     * ****************************************************************************************************************************************
     */

	 //retrieve messages grouped by person
	 $app->post('/messages', function() use($app) {
		 
		$response = array();
        $user = $app->request->getJsonRawBody();
        $user_id = $user->id;
		
        $query = "SELECT Messages.content AS content, Users.id AS sender_id, Messages.is_new AS is_new, Messages.created_at AS created_at, Users.name AS name FROM Messages INNER JOIN Users ON Messages.sender_id = Users.id WHERE receiver_id = :id: GROUP BY Users.id ORDER BY Messages.created_at DESC";
        $messages = $app->modelsManager->executeQuery($query, array(
            'id' => $user_id
        ));
        if ($messages->getFirst()) {
		$response[] = array(
                'error' => FALSE,
				'status' => 'Downloading successfull!'
            );
        foreach ($messages as $message) {
			
            $response[] = array(
                'name' => $message->name,
                'content' => $message->content,
                'sender_id' => $message->sender_id,
                'is_new' => $message->is_new,
                'created_at' => $message->created_at         
            );
		}
        } else {
            $response["error"] = TRUE;
            $response["error_msg"] = "Oops! No messages yet!";
        }

        echo json_encode($response);
    });
	 
	 
	 //retrieve all messages from given user
	 $app->post('/users/messages', function() use($app) {
		 
		$response = array();
        $user = $app->request->getJsonRawBody();
        $user_id = $user->id;
		$sender_id = $user->sender_id;
		$user_pid = $user->user_pid;
		if (!empty($user_id) && !empty($sender_id) && !empty($user_pid)) {
        $query = "SELECT Messages.content AS content, Messages.created_at AS created_at, Messages.sender_id AS sender_id FROM Messages WHERE (Messages.receiver_id = :id: AND Messages.sender_id = :sender_id:) OR (Messages.receiver_id = idIntoUniqueId(:sender_id:) AND Messages.sender_id = :user_pid:) ORDER BY Messages.created_at ASC";
        $messages = $app->modelsManager->executeQuery($query, array(
            'id' => $user_id,
			'user_pid' => $user_pid,
			'sender_id' => $sender_id
        ));
        if ($messages->getFirst()) {
		$response[] = array(
                'error' => FALSE,
				'status' => 'Downloading successfull!'
            );
        foreach ($messages as $message) {
			
            $response[] = array(
                'content' => $message->content,
                'sender_id' => $message->sender_id,
                'created_at' => $message->created_at         
            );
		}
        } else {
            $response["error"] = TRUE;
            $response["error_msg"] = "Oops! Something went wrong!";
        }
		}else {
			$response["error"] = TRUE;
            $response["error_msg"] = "Oops! Some data is missing!";
		}

        echo json_encode($response);
    });
	 
	 
	//send a message
	$app->post('/users/messages/id', function() use($app) {
		 
		$response = array();
        $user = $app->request->getJsonRawBody();
     
		if (!empty($user->content) && !empty($user->receiver_id) && !empty($user->user_pid)) {
		 // Create a response
        $response = array();
		
        $query = "INSERT INTO Messages (receiver_id ,  sender_id ,  content)  VALUES (idIntoUniqueId( " . $user->receiver_id ." ) , :user_pid:, :content: )";
        $message = $app->modelsManager->executeQuery($query, array(
			'user_pid' => $user->user_pid,
			'content' =>  $user->content
        ));
        if ($message->success()) {
		$response[] = array(
                'error' => FALSE,
				'status' => 'Send!'
            );
        } else {
            $response["error"] = TRUE;
            $response["error_msg"] = "Oops! Something went wrong!";
        }
		}else {
			$response["error"] = TRUE;
            $response["error_msg"] = "Oops! Some data is missing!";
		}

        echo json_encode($response);
    });
	
	
	
	    /**
     * **************************************************************** COMMENTS TABLE ********************************************************** 
     * *
     * ****************************************************************************************************************************************
     */
	 
	 
	 
	   //add the comment
    $app->post('/users/comments', function () use ($app) {

        $comment = $app->request->getJsonRawBody();
        $query = "INSERT INTO Comments (user_id, sender_id, content) VALUES (:user_id:, :sender_id:, :content:)";

        $status = $app->modelsManager->executeQuery($query, array(
            'user_id' => $comment->user_id,
            'sender_id' => $comment->sender_id,
            'content' => $comment->content,
           
        ));

        // Create a response
        $response = new Response();

        // Check if the insertion was successful
        if ($status->success() == true) {

            // Change the HTTP status
            $response->setStatusCode(200, "Created");

            $response->setJsonContent(
                    array(
						'error'=>FALSE,
                        'status' => 'Comment successfully added',
                    )
            );
        } else {

            // Change the HTTP status
            $response->setStatusCode(403, "Forbidden");

            // Send errors to the client
            $errors = array();
            foreach ($status->getMessages() as $message) {
                $errors[] = $message->getMessage();
            }

            $response->setJsonContent(
                    array(
                        'status' => 'An error occurred!',
                        'messages' => $errors
                    )
            );
        }

        return $response;
    });
	 
	 
	 
	 //retrieve all comments for given user
	  $app->post('/users/comments/id', function() use($app) {

		$user = $app->request->getJsonRawBody();
        $query = "SELECT Comments.content AS content, Comments.created_at AS created_at, Users.name AS name FROM Comments LEFT JOIN Users ON Comments.sender_id = Users.id WHERE Comments.user_id=:id: ORDER BY Comments.created_at ASC";
        $comments = $app->modelsManager->executeQuery($query, array(
            'id' => $user->user_id
        ));
        $response = array();
		if ($comments->getFirst()) {
		$response[] = array(
                'error' => FALSE,
				'status' => 'Downloading successfull!'
            );
        foreach ($comments as $comment) {
            $response[] = array(
                'name' => $comment->name,
                'created_at' => $comment->created_at,
                'content' => $comment->content
      
            );
        }
		} else {
			$response[] = array(
                'error' => TRUE,
				'error_msg' => 'Oops! No comments found!'
            );
        }

        echo json_encode($response);
    });
	 
	 
	    /**
     * **************************************************************** PROFILES TABLE ********************************************************** 
     * *
     * ****************************************************************************************************************************************
     */
	 
	 	 //retrieve profile for given user
	  $app->post('/users/profiles/id', function () use($app) {

		$user = $app->request->getJsonRawBody();
		$query = "SELECT Profiles.date_of_birth AS date_of_birth, getTotalDistanceById(:id:) AS mileage, Profiles.gender AS gender, Users.name AS name FROM Profiles INNER JOIN Users ON Profiles.user_id = Users.id WHERE Profiles.user_id=:id:";
		if(isset($user->user_id)) {
			
        $comments = $app->modelsManager->executeQuery($query, array(
            'id' => $user->user_id
        ));
        $response = array();
		if ($comments->getFirst()) {
			$comment = $comments->getFirst();
		$response[] = array(
                'error' => FALSE,
				'status' => 'Downloading successfull!',
				'name' =>$comment->name,
				'date_of_birth' => $comment->date_of_birth,
				'mileage' => $comment->mileage,
				'gender' => $comment->gender
			);
        
		} else {
			$response[] = array(
                'error' => TRUE,
				'error_msg' => 'Oops! Some error occurred!'
            );
        }
		} else {
				$response[] = array(
                'error' => TRUE,
				'error_msg' => 'User id is not set!'
				);
		}

        echo json_encode($response);
    });
	
	
	/**
     * **************************************************************** ATTENDINGS TABLE ********************************************************** 
     * *
     * ****************************************************************************************************************************************
     */
	
	
	//retrieve attendings for given training by it's tid
	 $app->post('/trainings/attendings', function() use($app) {
		 
		$response = array();
        $training = $app->request->getJsonRawBody();
        $tid = $training->tid;
		
        $query = "SELECT Users.name AS name, Users.id AS user_id, Attendings.id AS id FROM Attendings INNER JOIN Users ON Attendings.uid=Users.id WHERE Attendings.tid =:tid:";
        $attendings = $app->modelsManager->executeQuery($query, array(
            'tid' => $tid
        ));
        if ($attendings->getFirst()) {
		$response[] = array(
                'error' => FALSE,
				'status' => 'Downloading successfull!'
            );
        foreach ($attendings as $attending) {
			
            $response[] = array(
                'name' => $attending->name,
                'user_id' => $attending->user_id,
                'id' => $attending->id,
            );
		}
        } else {
            $response["error"] = TRUE;
            $response["error_msg"] = "Oops! Nobody attending yet!";
        }

        echo json_encode($response);
    });
	
	
	// Deletes attending based on user_id and tid
    $app->post('/trainings/attendings/id', function () use ($app) {

		$training = $app->request->getJsonRawBody();
        $tid = $training->tid;
		$uid = $training->uid;
		
        $phql = "DELETE FROM Attendings WHERE tid = :tid: AND uid = :uid:";
        $status = $app->modelsManager->executeQuery($phql, array(
            'tid' => $tid,
			'uid' => $uid
        ));

        // Create a response
        $response = new Response();

        if ($status->success() == true) {
            $response->setJsonContent(
                    array(
						'error' =>FALSE,
                        'status' => 'Attending successfully deleted!'
                    )
            );
        } else {

            // Change the HTTP status
            $response->setStatusCode(403, "Forbidden");

            $errors = array();
            foreach ($status->getMessages() as $message) {
                $errors[] = $message->getMessage();
            }

            $response->setJsonContent(
                    array(
						'error' =>TRUE,
                        'status' => 'An error occurred!',
                        'messages' => $errors
                    )
            );
        }

        return $response;
    });
	
	
	  //add the attendence
    $app->post('/trainings/attendings/users/id', function () use ($app) {

        $user = $app->request->getJsonRawBody();
        $query = "INSERT INTO Attendings (tid, uid) VALUES (:tid:, :uid:)";

        $status = $app->modelsManager->executeQuery($query, array(
            'tid' => $user->tid,
            'uid' => $user->uid           
        ));

        // Create a response
        $response = new Response();

        // Check if the insertion was successful
        if ($status->success() == true) {

            // Change the HTTP status
            $response->setStatusCode(200, "Created");

            $response->setJsonContent(
                    array(
						'error'=>FALSE,
                        'status' => 'Attending successfully added',
                    )
            );
        } else {

            // Change the HTTP status
            $response->setStatusCode(403, "Forbidden");

            // Send errors to the client
            $errors = array();
            foreach ($status->getMessages() as $message) {
                $errors[] = $message->getMessage();
            }

            $response->setJsonContent(
                    array(
						'error'=>TRUE,
                        'status' => 'An error occurred!',
                        'messages' => $errors
                    )
            );
        }

        return $response;
    });
	 
	
		/**
     * **************************************************************** STATISTICS TABLE ********************************************************** 
     * *
     * ****************************************************************************************************************************************
     */
	 
	 
		//retrieve statistics for given user
	  $app->post('/users/statistics', function () use($app) {

		$user = $app->request->getJsonRawBody();
		$query = "SELECT getTotalDistanceById(:id:) AS total_distance, getMaxDistanceById(:id:) AS max_distance, getAverageDistanceByGender('Male') AS average_distance_male, getAverageDistanceByGender('Female') AS average_distance_female, getNewUserCountByGender('Male') AS new_user_count_male, getNewUserCountByGender('Female') AS new_user_count_female FROM Profiles";
		if(isset($user->user_id)) {
			
        $statistics = $app->modelsManager->executeQuery($query, array(
            'id' => $user->user_id
        ));
        $response = array();
		if ($statistics->getFirst()) {
			$statistic = $statistics->getFirst();
		$response[] = array(
                'error' => FALSE,
				'status' => 'Downloading successfull!',
				'total_distance' =>$statistic->total_distance,
				'max_distance' => $statistic->max_distance,
				'average_distance_male' => $statistic->average_distance_male,
				'average_distance_female' => $statistic->average_distance_female,
				'new_user_count_male' => $statistic->new_user_count_male,
				'new_user_count_female' => $statistic->new_user_count_female
			);
        
		} else {
			$response[] = array(
                'error' => TRUE,
				'error_msg' => 'Oops! Some error occurred!'
            );
        }
		} else {
				$response[] = array(
                'error' => TRUE,
				'error_msg' => 'User id is not set!'
				);
		}

        echo json_encode($response);
    });
	
	
	

    $app->notFound(function () use ($app) {
        $app->response->setStatusCode(404, "Not Found")->sendHeaders();
        echo 'Sorry, this page was not found!';
    });


    /**
     * Handle the request
     */
    $app->handle();
} catch (\Exception $e) {
    echo $e->getMessage(), PHP_EOL;
    echo $e->getTraceAsString();
}
?>