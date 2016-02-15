<?php

use Phalcon\Mvc\Model;
use Phalcon\Mvc\Model\Message;
use Phalcon\Mvc\Model\Validator\Uniqueness;
use Phalcon\Mvc\Model\Validator\InclusionIn;

class Attendings extends Model
{
    public $id;
	public $tid;
    public $uid;
	public $updated_at;
    public $created_at;
	
}
?>