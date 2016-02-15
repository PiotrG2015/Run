<?php

use Phalcon\Mvc\Model;
use Phalcon\Mvc\Model\Message;
use Phalcon\Mvc\Model\Validator\Uniqueness;
use Phalcon\Mvc\Model\Validator\InclusionIn;

class Comments extends Model
{
    public $content;
	public $cid;
    public $sender_id;
	public $user_id;
    public $created_at;
	
}
?>