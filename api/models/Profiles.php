<?php

use Phalcon\Mvc\Model;
use Phalcon\Mvc\Model\Message;
use Phalcon\Mvc\Model\Validator\Uniqueness;
use Phalcon\Mvc\Model\Validator\InclusionIn;

class Profiles extends Model
{
	public $pid;
    public $user_id;
	public $date_of_birth;
	public $gender;
    public $updated_at;
    public $created_at;
	
}
?>