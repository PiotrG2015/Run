<?php

use Phalcon\Mvc\Model;
use Phalcon\Mvc\Model\Message;
use Phalcon\Mvc\Model\Validator\Uniqueness;
use Phalcon\Mvc\Model\Validator\InclusionIn;

class Trainings extends Model
{
	public $distance;
	public $place;
	public $user_id;
	public $name;
	public $tid;
	public $description;
	public $time_of_training;
	
	/*public function initialize()
    {
        $this->belongsTo("user_id", "Users", "unique_id");
    }*/
	
    public function validation()
    {
        
        // Year cannot be less than zero
        if ($this->distance < 0) {
            $this->appendMessage(new Message("The distance cannot be less than zero"));
        }

        // Check if any messages have been produced
        if ($this->validationHasFailed() == true) {
            return false;
        }
    }
}
?>