<?php

use Phalcon\Mvc\Model;
use Phalcon\Mvc\Model\Message;
use Phalcon\Mvc\Model\Validator\Uniqueness;
use Phalcon\Mvc\Model\Validator\InclusionIn;

class Users extends Model
{
	public $id;
	public $unique_id;
	public $name;
	public $email;
	public $encrypted_password;
	public $created_at;
	public $updated_at;
	
	
	/*public function initialize()
    {
        $this->hasMany("user_id", "Trainings", "unique_id");
    }*/
	
    public function validation()
    {
        
        // Year cannot be less than zero
        if ($this->name == null) {
            $this->appendMessage(new Message("The name cannot be empty"));
        }
		if ($this->email == null) {
            $this->appendMessage(new Message("The email cannot be empty"));
        }
		
		if ($this->encrypted_password == null) {
            $this->appendMessage(new Message("The password cannot be empty"));
        }

        // Check if any messages have been produced
        if ($this->validationHasFailed() == true) {
            return false;
        }
    }
}
?>